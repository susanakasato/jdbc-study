package model.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJdbc implements DepartmentDao {
	
	private Connection connection = null;
	
	public DepartmentDaoJdbc(Connection connection) {
		this.connection = connection;
	}
	
	@Override
	public void insert(Department department) {
		String query = "INSERT INTO DEPARTMENT (NAME) VALUES (?)";
		PreparedStatement preparedStatement = null;
		ResultSet generatedIds = null;
		try {
			preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, department.getName());
			int rowsCount = preparedStatement.executeUpdate();
			if (rowsCount > 0) {
				generatedIds = preparedStatement.getGeneratedKeys();
				generatedIds.next();
				department.setId(generatedIds.getInt(1));
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(generatedIds);
			DB.closeStatement(preparedStatement);
		}
	}

	@Override
	public void update(Department department) {
		String query = "UPDATE DEPARTMENT SET NAME = ? WHERE ID = ?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, department.getName());
			preparedStatement.setInt(2, department.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		String query = "DELETE FROM DEPARTMENT WHERE ID = ?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
		}
	}

	@Override
	public Department findById(Integer id) {
		String query = "SELECT * FROM DEPARTMENT WHERE ID = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) return  new Department(id, resultSet.getString("name"));
			else return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(resultSet);
			DB.closeStatement(preparedStatement);
		}
		
	}

	@Override
	public List<Department> findAll() {
		String query = "SELECT * FROM DEPARTMENT";
		Statement statement = null;
		ResultSet resultSet = null;
		List<Department> departments = new ArrayList<Department>();
		try {
			statement = connection.createStatement();
			statement.execute(query);
			resultSet = statement.getResultSet();
			while (resultSet.next()) {
				departments.add(new Department(
						resultSet.getInt("id"),
						resultSet.getString("name")));
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(resultSet);
			DB.closeStatement(statement);
		}
		
		
		return departments;
	}

}

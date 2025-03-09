package model.dao.implementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJdbc implements SellerDao{
	
	private Connection connection = null;
	
	public SellerDaoJdbc(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Seller seller) {
		String query = "INSERT INTO SELLER (NAME, EMAIL, BIRTH_DATE, BASE_SALARY, DEPARTMENT_ID) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStatement.setString(1, seller.getName());
			preparedStatement.setString(2, seller.getEmail());
			preparedStatement.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			preparedStatement.setDouble(4, seller.getBaseSalary());
			preparedStatement.setInt(5, seller.getDepartment().getId());
			int rowsCount = preparedStatement.executeUpdate();
			if (rowsCount > 0) {
				resultSet = preparedStatement.getGeneratedKeys();
				resultSet.next();
				Integer generatedId = resultSet.getInt(1);
				seller.setId(generatedId);
			}
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(resultSet);
			DB.closeStatement(preparedStatement);
		}
	}

	@Override
	public void update(Seller seller) {
		String query = "UPDATE SELLER SET NAME = ?, EMAIL = ?, BIRTH_DATE = ?, BASE_SALARY = ?, DEPARTMENT_ID = ? WHERE ID = ?";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, seller.getName());
			preparedStatement.setString(2, seller.getEmail());
			preparedStatement.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			preparedStatement.setDouble(4, seller.getBaseSalary());
			preparedStatement.setInt(5, seller.getDepartment().getId());
			preparedStatement.setInt(6, seller.getId());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(preparedStatement);
		}
	}

	@Override
	public void deleteById(Integer id) {
		String query = "DELETE FROM SELLER WHERE ID = ?";
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
	public Seller findById(Integer id) {
		String query = "SELECT SELLER.*, DEPARTMENT.NAME AS DEPARTMENT_NAME FROM SELLER INNER JOIN DEPARTMENT ON SELLER.DEPARTMENT_ID = DEPARTMENT.ID WHERE SELLER.ID = ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return getNewSellerFromResultSet(resultSet, getNewDepartmentFromResultSet(resultSet));
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(resultSet);
			DB.closeStatement(preparedStatement);
		}
		
	}

	@Override
	public List<Seller> findAll() {
		String query = "SELECT SELLER.*, DEPARTMENT.NAME AS DEPARTMENT_NAME FROM SELLER INNER JOIN DEPARTMENT ON SELLER.DEPARTMENT_ID = DEPARTMENT.ID";
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			statement.execute(query);
			resultSet = statement.getResultSet();
			List<Seller> sellers = new ArrayList<Seller>();
			HashMap<Integer, Department> departments = new HashMap<Integer, Department>();
			while(resultSet.next()) {
				Integer departmentId = resultSet.getInt("department_id");
				Department department = departments.get(departmentId);
				if (department == null) {
					department = getNewDepartmentFromResultSet(resultSet);
					departments.put(departmentId, department);
				}
				sellers.add(getNewSellerFromResultSet(resultSet, department));
			}
			return sellers;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(resultSet);
			DB.closeStatement(statement);
		}
	}
	
	private Seller getNewSellerFromResultSet(ResultSet resultSet, Department department) throws SQLException {
		return new Seller(
				resultSet.getInt("id"), 
				resultSet.getString("name"), 
				resultSet.getString("email"), 
				resultSet.getDate("birth_date"), 
				resultSet.getDouble("base_salary"), 
				department);
			
	}
	
	private Department getNewDepartmentFromResultSet(ResultSet resultSet) throws SQLException {
		return new Department(
				resultSet.getInt("department_id"),
				resultSet.getString("department_name"));
	}

}

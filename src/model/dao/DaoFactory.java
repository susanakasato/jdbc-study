package model.dao;

import db.DB;
import model.dao.implementation.DepartmentDaoJdbc;

public class DaoFactory {
	
	public static DepartmentDao getDepartmentDao() {
		return new DepartmentDaoJdbc(DB.getDatabaseConnection());
	}
	
}

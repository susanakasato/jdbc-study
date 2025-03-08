package model.dao;

import db.DB;
import db.implementation.DepartmentDaoJdbc;

public class DaoFactory {
	
	public static DepartmentDao getDepartmentDao() {
		return new DepartmentDaoJdbc(DB.getDatabaseConnection());
	}
	
}

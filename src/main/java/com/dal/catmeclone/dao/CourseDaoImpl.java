package com.dal.catmeclone.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dal.catmeclone.DBUtility.DatabaseConnection;
import com.dal.catmeclone.exceptionhandler.UserDefinedSQLException;
import com.dal.catmeclone.model.Course;

public class CourseDaoImpl implements CourseDao{

	DatabaseConnection db = new DatabaseConnection();
	private CallableStatement statement;
	private Connection con;
	private ResultSet rs;
	List<Course> c;

	@Override
	public List<Course> getAllCourses() throws SQLException, UserDefinedSQLException{
		c = new ArrayList<Course>();
		con = db.connect();
		try {
		statement = con.prepareCall("{CALL GetAllCourses()}");
		rs = statement.executeQuery();
		while(rs.next()) {
			c.add(new Course(rs.getInt(1),rs.getString(2)));
		}
		}
		catch(Exception e) {
			System.out.println("Unable to execute query");
		}
		finally
		{
			if (statement != null)
			{
				statement.close();
			}
			if (con != null)
			{
				if (!con.isClosed())
				{
					con.close();
				}
			}
		}
		return c;
	}

	@Override
	public boolean deleteCourse(int courseID) throws SQLException, UserDefinedSQLException {
		// TODO Auto-generated method stub
		// Connect to database
				con = db.connect();
				statement = con.prepareCall("{CALL DeleteCourse(?)}");
				
				try {
					statement.setInt(1, courseID);
					statement.execute();
				}
				catch(Exception e) {
					
					//Logging needed
					return false;
				}
				finally
				{
					if (statement != null)
					{
						statement.close();
					}
					if (con != null)
					{
						if (!con.isClosed())
						{
							con.close();
						}
					}
				}
				return true;
	}

	@Override
	public boolean insertCourse(Course course) throws SQLException, UserDefinedSQLException {
		// TODO Auto-generated method stub
		
		// Connect to database
		con = db.connect();
		statement = con.prepareCall("{CALL Createcourse(?,?)}");
		
		try {
			statement.setInt(1, course.getCourseID());
			statement.setString(2, course.getCourseName());
			statement.execute();
		}
		catch(Exception e) {
			
			//Logging needed
			return false;
		}
		finally
		{
			if (statement != null)
			{
				statement.close();
			}
			if (con != null)
			{
				if (!con.isClosed())
				{
					con.close();
				}
			}
		}
		return true;
	}
}

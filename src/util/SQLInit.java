package util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class SQLInit {
	private static String driver;
	private static String url;
	private static String user;
	private static String pass;
	private static Connection conn;
	
	public static void initParam(String paramFile) throws Exception{
		Properties props = new Properties();
		props.load(new FileInputStream(paramFile));
		driver = props.getProperty("driver");
		url = props.getProperty("url");
		user = props.getProperty("user");
		pass = props.getProperty("pass");
		Class.forName(driver);
		conn = DriverManager.getConnection(url, user, pass);
	}
	
	public static String getDriver(){
		return driver;
	}
	
	public static String getUrl(){
		return url;
	}
	
	public static String getUser(){
		return user;
	}
	
	public static String getPass(){
		return pass;
	}
	
	public static Connection getConn(){
		return conn;
	}
	
	public static void closeSQL(Connection conn, ResultSet rs, PreparedStatement stmt) throws Exception{
		if(conn != null){
			conn.close();
		}
		if(rs != null){
			rs.close();
		}
		if(stmt != null){
			stmt.close();
		}
	}
}

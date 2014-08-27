package util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WriteToSQL{
	public static void write(String sql, Connection conn, String str, int id) throws Exception{
		try{
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, str);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void write(String sql, Connection conn, String distance, String weight, int id) throws Exception{
		try{
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, distance);
			pstmt.setString(2, weight);
			pstmt.setInt(3, id);
			pstmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void write(String sql, Connection conn, int id, String cclass, String user_content, Date date) throws Exception{
		try{
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setString(2, cclass);
			pstmt.setString(3, user_content);
			pstmt.setDate(4, date);
			pstmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void write(String sql, Connection conn, int id, String user_content, Date date) throws Exception{
		try{
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, id);
			pstmt.setString(2, user_content);
			pstmt.setDate(3, date);
			pstmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}
package util;

import java.sql.Connection;
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
}
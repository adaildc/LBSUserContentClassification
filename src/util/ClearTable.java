package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClearTable {
	public static void clearTable(Connection conn)throws Exception{
		try{
			PreparedStatement pstmt = conn.prepareStatement("truncate table lbs2");
			pstmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}

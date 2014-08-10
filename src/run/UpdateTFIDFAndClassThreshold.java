package run;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import util.ReadFromSQL;
import util.SQLInit;
import model.TFIDF;

public class UpdateTFIDFAndClassThreshold {
	//在分类之前，先把样本的tfidf更新
	public static void main(String[] args) throws Exception {
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		int total_num = 0;

		try {
			String sql = "select count(*) from lbs_sample";
			SQLInit.initParam("mysql.properties");
			conn = SQLInit.getConn();
			rs = ReadFromSQL.query(sql,conn);
			total_num = rs.getInt(1);
			System.out.println(total_num);
			ArrayList<String> viawList = TFIDF.toTFtoIDF();
			TFIDF.toTFIDF(total_num);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			SQLInit.closeSQL(conn, rs, stmt);
		}
	}
}

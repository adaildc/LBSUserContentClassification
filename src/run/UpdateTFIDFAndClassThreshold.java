package run;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import util.ReadFromSQL;
import util.SQLInit;
import model.GetClassThreshold;
import model.TFIDF;

public class UpdateTFIDFAndClassThreshold {
	//在分类之前，先把样本的tfidf更新
	public static void main(String[] args) throws Exception {
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		int total_num = 0;

		try {
			String sql = "select * from lbs_sample";
			SQLInit.initParam("mysql.properties");
			conn = SQLInit.getConn();
			rs = ReadFromSQL.query(sql,conn);
			rs.last();
			total_num = rs.getRow();
			ArrayList<String> viawList = TFIDF.toTFtoIDF();
			TFIDF.toTFIDF(total_num);
			
			rs.beforeFirst();
			GetClassThreshold.getClassThreshold(rs, conn, viawList, 5);
			System.out.println("准备阶段完成！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			SQLInit.closeSQL(conn, rs, stmt);
		}
	}
}

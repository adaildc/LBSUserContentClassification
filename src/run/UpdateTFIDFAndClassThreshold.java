package run;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import util.ReadFromSQL;
import util.SQLInit;
import model.TFIDF;

public class UpdateTFIDFAndClassThreshold {
	//�ڷ���֮ǰ���Ȱ�������tfidf�������ֵ����
	public static void main(String[] args) throws Exception {
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		int total_num = 0;
		int id = 0;
		String cclass = "";
		String content = "";
		String tfidf = "";
		HashMap<String, Double> tfidfmap = new HashMap<>();
		try {
			String sql = "select * from lbs_sample";
			SQLInit.initParam("mysql.properties");
			conn = SQLInit.getConn();
			rs = ReadFromSQL.query(sql,conn);
			rs.last();
			total_num = rs.getRow();
			System.out.println(total_num);
			TFIDF.toTFtoIDF();
			TFIDF.toTFIDF(total_num);
			
			rs.beforeFirst();
			while(rs.next()){
				cclass = rs.getString("class");
				content = rs.getString("user_content");
				tfidf = rs.getString("tfidf");
				tfidfmap = TFIDF.strToMap(tfidf);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			SQLInit.closeSQL(conn, rs, stmt);
		}
	}
}
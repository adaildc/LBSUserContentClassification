package run;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import struct.ClassCenterThreshold;
import struct.VectorItemsAndWeight;
import util.ReadFromSQL;
import util.SQLInit;
import model.ClassThreshold;
import model.TFIDF;

public class UpdateTFIDFAndClassThreshold {
	//在分类之前，先把样本的tfidf和类别阈值更新
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
			ArrayList<VectorItemsAndWeight> viawList = TFIDF.toTFtoIDF();
			TFIDF.toTFIDF(total_num);
			
			rs.beforeFirst();
			ArrayList<ClassCenterThreshold> centhr = ClassThreshold.getClassCenterThreshold(rs, viawList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			SQLInit.closeSQL(conn, rs, stmt);
		}
	}
}

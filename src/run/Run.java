package run;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.MyKnn;
import model.TFIDF;
import struct.ClassThreshold;
import util.ClearTable;
import util.ReadFromSQL;
import util.SQLInit;
import util.Share;

public class Run {
	public static void run() throws Exception{
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		BufferedReader br = null;
		ArrayList<String> viawList = new ArrayList<>();
		
		
		try{
			String sql = "select * from lbs";
			String num_sql = "select * from lbs_sample";
			String write_class_sql = "insert into lbs2 (Id, class, user_content, date) values (?,?,?,?)";
			String write_abnormal_sql = "insert into lbs2 (Id, class, user_content, date) values (?,?,?,?)";
			SQLInit.initParam("mysql.properties");
			conn = SQLInit.getConn();
			
			String str = "";
			String[] s = new String[2];
			br = new BufferedReader(new FileReader("idf.txt"));
			while((str = br.readLine())!=null){
				s = str.split("    ");
				viawList.add(s[0]);
			}
			
			rs = ReadFromSQL.query(num_sql,conn);
			ArrayList<ClassThreshold> samples = new ArrayList<>();
			while(rs.next()){
				String cclass = rs.getString("class");
				String tfidf = rs.getString("tfidf");
				HashMap<String,Double> tfidfmap = TFIDF.strToMap(tfidf);
				ArrayList<Double> point = Share.getVector(tfidfmap, viawList);
				String k_distance = rs.getString("k_distance");
				double kd = Double.parseDouble(k_distance);
				ClassThreshold ct = new ClassThreshold(cclass, point, kd);
				samples.add(ct);
			}
			int total_num = rs.getRow();
			
			rs = ReadFromSQL.query(sql,conn);
			ClearTable.clearTable(conn);
			int sum = 0;
			while(rs.next()){
				String sss = MyKnn.runMyKnn(rs, total_num, write_class_sql, write_abnormal_sql, conn, samples, viawList);
				if(!sss.equals("")){
					sum++;
				}
			}
			System.out.print(sum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			SQLInit.closeSQL(conn, rs, stmt);
		}
	}
	
	public static void main(String[] args) throws Exception {
		run();
	}
}

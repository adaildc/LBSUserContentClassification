package run;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
			SimpleDateFormat time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date sd = new Date();
			String start = time.format(sd);
			System.out.println("开始时间为：" + start);
			String sql = "select * from lbs";
			String num_sql = "select * from lbs_sample";
			String write_class_sql = "insert into lbs2 (Id, class, user_content, date) values (?,?,?,?)";
			String write_abnormal_sql = "insert into lbs1 (Id, user_content, date) values (?,?,?)";
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
				String weight = rs.getString("weight");
				double wt = Double.parseDouble(weight);
				ClassThreshold ct = new ClassThreshold(cclass, point, kd, wt);
				samples.add(ct);
			}
			int total_num = rs.getRow();
			
			rs = ReadFromSQL.query(sql,conn);
			ClearTable.clearTable(conn);
			int sum1 = 0;
			int sum2 = 0;
			while(rs.next()){
				String sss = MyKnn.runMyKnn(rs, total_num, write_class_sql, write_abnormal_sql, conn, samples, viawList);
				if(!sss.equals("")){
					sum1++;
				}else{
					sum2++;
				}
			}
			Date ed = new Date();
			String end = time.format(ed);
			Calendar ca1 = Calendar.getInstance();
			Calendar ca2 = Calendar.getInstance();
			ca1.setTime(sd);
			ca2.setTime(ed);
			int distanceMin = ca2.get(Calendar.MINUTE) - ca1.get(Calendar.MINUTE);
			System.out.println("完成，时间为：" + end);
			System.out.println("分类所花时长：" + distanceMin);
			System.out.println("分类结果个数：" + sum1);
			System.out.println("离群点个数：" + sum2);
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

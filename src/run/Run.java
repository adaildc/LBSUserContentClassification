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
import struct.ClassCenterThreshold;
import util.ClearTable;
import util.ReadFromSQL;
import util.SQLInit;

public class Run {
	public static void run() throws Exception{
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		BufferedReader br = null;
		
		try{
			String sql = "select * from lbs";
			String num_sql = "select count(*) from lbs_sample";
			String write_sql = "insert into lbs2 (Id, class, user_content, date) values (?,?,?,?)";
			SQLInit.initParam("mysql.properties");
			conn = SQLInit.getConn();
			rs = ReadFromSQL.query(num_sql,conn);
			rs.next();
			int total_num = rs.getInt(1);
			rs = ReadFromSQL.query(sql,conn);
			br = new BufferedReader(new FileReader("class.txt"));
			String str = "";
			String[] s = new String[3];
			ArrayList<ClassCenterThreshold> cctList = new ArrayList<>();
			String substr = "";
			while((str = br.readLine())!=null){
				s = str.split("    ");
				ArrayList<Double> center = new ArrayList<>();
				substr = s[1].substring(1, s[1].length()-1);
				String[] ss = substr.split(", ");
				for(int i=0;i<ss.length;i++){
					center.add(Double.parseDouble(ss[i]));
				}
				ClassCenterThreshold cct = new ClassCenterThreshold(s[0], center, Double.parseDouble(s[2]));
				cctList.add(cct);
			}
			ClearTable.clearTable(conn);
			int sum = 0;
			while(rs.next()){
				String sss = MyKnn.knn(rs, total_num, cctList, write_sql, conn);
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
			br.close();
		}
	}
	
	public static void main(String[] args) throws Exception {
		run();
	}
}

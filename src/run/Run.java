package run;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;

import model.TFIDF;

import util.ReadFromSQL;
import util.SQLInit;

public class Run {
	public static void run() throws Exception{
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		HashMap<String,Double> tfmap = new HashMap<>();
		HashMap<String,Double> tfidfmap = new HashMap<>();
		try{
			String sql = "select * from lbs";
			String num_sql = "select count(*) from lbs_sample";
			SQLInit.initParam("mysql.properties");
			conn = SQLInit.getConn();
			rs = ReadFromSQL.query(num_sql,conn);
			rs.next();
			int total_num = rs.getInt(1);
			rs = ReadFromSQL.query(sql,conn);
			int i = 0;
			while(rs.next()){
				tfmap = TFIDF.tf(rs);
				tfidfmap = TFIDF.tfidf(rs, tfmap, total_num);
				//分类运算 和 查出奇异点的运算
				Iterator<String> it = tfidfmap.keySet().iterator();
				System.out.println();
				System.out.print(++i);
				while(it.hasNext()){
					String key = it.next();
					System.out.print(key+":"+tfidfmap.get(key)+";;");
				}
			}
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

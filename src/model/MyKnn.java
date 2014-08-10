package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import util.Share;
import util.WriteToSQL;

public class MyKnn {
	public static String knn(ResultSet rs, int total_num, String sql, Connection conn) throws Exception{
		HashMap<String,Double> tfmap = new HashMap<>();
		HashMap<String,Double> tfidfmap = new HashMap<>();
		BufferedReader br = null;
		ArrayList<String> viawList = new ArrayList<>();
		String cclass = "";
		int id = 0;
		String user_content = "";
		Date date = null;
		
		try {
			tfmap = TFIDF.tf(rs);
			tfidfmap = TFIDF.tfidf(rs, tfmap, total_num);
			String str = "";
			String[] s = new String[2];
			br = new BufferedReader(new FileReader("idf.txt"));
			while((str = br.readLine())!=null){
				s = str.split("    ");
				viawList.add(s[0]);
			}
			ArrayList<Double> point = Share.getVector(tfidfmap, viawList);
			id = rs.getInt("Id");
			user_content = rs.getString("user_content");
			date = rs.getDate("date");
			if(tfidfmap.size() != 0){
				//��Ⱥ����
				//knn����
				
			}else{
				System.out.println(user_content);
				//����˵���û�������û�м�ֵ����Ϣ�������û���������
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			br.close();
		}
		return cclass;
	}
	
}

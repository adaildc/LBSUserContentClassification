package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import struct.ClassCenterThreshold;
import util.Share;
import util.WriteToSQL;

public class MyKnn {
	public static String knn(ResultSet rs, int total_num, ArrayList<ClassCenterThreshold> cctList, String sql, Connection conn) throws Exception{
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
			cclass = getClassByThreshold(point, cctList);
			id = rs.getInt("Id");
			user_content = rs.getString("user_content");
			date = rs.getDate("date");
			if(!cclass.equals("")){
				//写数据库操作，看能不能通过自己写的类WriteToSQL实现
				WriteToSQL.write(sql, conn, id, cclass, user_content, date);
			}else{
				//用knn算法分类
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			br.close();
		}
		return cclass;
	}
	
	public static String getClassByThreshold(ArrayList<Double> point, ArrayList<ClassCenterThreshold> cctList) throws Exception{
		String cclass = "";
		ArrayList<Double> center = null;
		double threshold = 0.0;
		ClassCenterThreshold cct = null;
		double distance = 0.0;
		double min = -10.0;
		String minclass = "";
		for(int i=0;i<cctList.size();i++){
			cct = cctList.get(i);
			cclass = cct.getCclass();
			center = cct.getCenter();
			threshold = cct.getThreshold();
			distance = Share.getDistance(center, point);
			if(distance < threshold && (min == -10.0 || min > distance)){
				min = distance;
				minclass = cclass;
			}
		}
		return minclass;
	}
}

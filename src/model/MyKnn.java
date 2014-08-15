package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import struct.ClassThreshold;
import util.Share;
import util.WriteToSQL;

public class MyKnn {
	public static String runMyKnn(ResultSet rs, int total_num, String sql, String sql1, Connection conn, ArrayList<ClassThreshold> samples, ArrayList<String> viawList) throws Exception{
		HashMap<String,Double> tfmap = new HashMap<>();
		HashMap<String,Double> tfidfmap = new HashMap<>();
		String cclass = "";
		int id = 0;
		String user_content = "";
		Date date = null;
		
		try {
			tfmap = TFIDF.tf(rs);
			tfidfmap = TFIDF.tfidf(rs, tfmap, total_num);
			ArrayList<Double> point = Share.getVector(tfidfmap, viawList);
			id = rs.getInt("Id");
			user_content = rs.getString("user_content");
			date = rs.getDate("date");
			if(tfidfmap.size() != 0){
				//离群点检测
				//knn分类
				cclass = myKnn(point, samples);
				if(!cclass.equals("")){
					WriteToSQL.write(sql, conn, id, cclass, user_content, date);
				}else{
					WriteToSQL.write(sql1, conn, id, user_content, date);
				}
				
			}else{
				//System.out.println(user_content);
				//这里说明用户发送了没有价值的信息，反馈用户重新输入
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cclass;
	}
	
	public static String myKnn(ArrayList<Double> point, ArrayList<ClassThreshold> samples) throws Exception{
		String cclass = "";
		try {
			ArrayList<ClassThreshold> points = getKNearestPoint(point, samples);
			HashMap<String, Integer> hashmap = new HashMap<>();
			String cc = "";
			String key = "";
			int num = points.size();
			int max = 0;
			int value = 0;
			if(num > 3){
				for(int i=0;i<num;i++){
					ClassThreshold ct = points.get(i);
					cc = ct.getCclass();
					if(hashmap.containsKey(cc)){
						hashmap.put(cc, hashmap.get(cc) + 1);
					}else{
						hashmap.put(cc, 1);
					}
				}
				Iterator<String> it = hashmap.keySet().iterator();
				while(it.hasNext()){
					key = it.next();
					value = hashmap.get(key);
					if(value > max){
						max = value;
						cc = key;
					}
				}
			}
			cclass = cc;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cclass;
	}
	
	public static ArrayList<ClassThreshold> getKNearestPoint(ArrayList<Double> point, ArrayList<ClassThreshold> samples) throws Exception{
		ArrayList<ClassThreshold> points = new ArrayList<>();
		double distance = 0.0;
		try {
			ClassThreshold ctp = null;
			Iterator<ClassThreshold> it = samples.iterator();
			int num = 0;
			while(it.hasNext()){
				ctp = it.next();
				ArrayList<Double> p = ctp.getPoint();
				double ts = ctp.getThreshold();
				distance = Share.getDistance(point, ctp.getPoint());
				num = points.size();
				if(distance <= ts){
					if(num == 0){
						ClassThreshold ct = new ClassThreshold(ctp.getCclass(), p, distance);
						points.add(ct);
					}else{
						if(distance >= points.get(num-1).getThreshold()){
							ClassThreshold ct = new ClassThreshold(ctp.getCclass(), p, distance);
							points.add(num, ct);
						}else{
							for(int i=0;i<num;i++){
								if(distance <= points.get(i).getThreshold()){
									ClassThreshold ct = new ClassThreshold(ctp.getCclass(), p, distance);
									points.add(i, ct);
									break;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return points;
	}
}

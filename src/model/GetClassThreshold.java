package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import util.Share;
import util.WriteToSQL;

public class GetClassThreshold {
	private static class IdTFIDF{
		private int id;
		private HashMap<String, Double> tfidfmap;
		
		public IdTFIDF(){
			
		}
		
		public IdTFIDF(int id, HashMap<String, Double> tfidfmap){
			this.id = id;
			this.tfidfmap = tfidfmap;
		}
		
		public int getId(){
			return this.id;
		}
		public HashMap<String, Double> getTfidfMap(){
			return this.tfidfmap;
		}
	}
	
	private static class IdPoint{
		private int id;
		private ArrayList<Double> point;
		
		public IdPoint(){
			
		}
		
		public IdPoint(int id, ArrayList<Double> point){
			this.id = id;
			this.point = point;
		}
		
		public int getId(){
			return id;
		}
		
		public ArrayList<Double> getPoint(){
			return this.point;
		}
	}
	
	public static void getClassThreshold(ResultSet rs, Connection conn, ArrayList<String> viawList, int k) throws Exception {
		String sql = "update lbs_sample set k_distance = ? where Id = ?";
		
		try {
			int id = 0;
			String cclass = "";
			String tfidf = "";
			HashMap<String, Double> tfidfmap = new HashMap<>();
			HashMap<String, ArrayList<IdTFIDF>> hashmap = new HashMap<>();
			
			while(rs.next()){
				id = rs.getInt("Id");
				cclass = rs.getString("class");
				tfidf = rs.getString("tfidf");
				tfidfmap = TFIDF.strToMap(tfidf);
				
				if(hashmap.containsKey(cclass)){
					ArrayList<IdTFIDF> details = hashmap.get(cclass);
					IdTFIDF idtfidf = new IdTFIDF(id, tfidfmap);
					details.add(idtfidf);
					hashmap.put(cclass, details);
				}else{
					ArrayList<IdTFIDF> details = new ArrayList<>();
					IdTFIDF idtfidf = new IdTFIDF(id, tfidfmap);
					details.add(idtfidf);
					hashmap.put(cclass, details);
				}
			}
			
			Iterator<String> it = hashmap.keySet().iterator();
			while(it.hasNext()){
				//一次循环是对一个类别进行阈值的计算
				double distance = 0.0;
				ArrayList<IdPoint> points = new ArrayList<>();
				cclass = it.next();
				ArrayList<IdTFIDF> details = hashmap.get(cclass);
				for(int i=0;i<details.size();i++){
					IdPoint idp = new IdPoint(details.get(i).getId(), Share.getVector(details.get(i).getTfidfMap(),viawList));
					points.add(idp);
				}
				IdPoint point = null;
				Iterator<IdPoint> pit = points.iterator();
				while(pit.hasNext()){
					point = pit.next();
					id = point.getId();
					distance = getKNearestDistance(k, point.getPoint(), points);
					if(distance < 0.5){
						distance = 0.5;
					}
					WriteToSQL.write(sql, conn, ""+distance, id);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double getKNearestDistance(int k, ArrayList<Double> point, ArrayList<IdPoint> points) throws Exception {
		double distance = 0.0;
		try {
			ArrayList<Double> d = new ArrayList<>(k);
			IdPoint p = null;
			Iterator<IdPoint> it = points.iterator();
			int num = 0;
			while(it.hasNext()){
				p = it.next();
				distance = Share.getDistance(point, p.getPoint());
				num = d.size();
				if(num == 0){
					d.add(distance);
				}else{
					if(distance >= d.get(num-1)){
						if(num < k){
							d.add(num, distance);
						}
					}else{
						for(int i=0;i<num;i++){
							if(distance <= d.get(i)){
								d.add(i, distance);
								if(num+1 > k){
									d.remove(k);
								}
								break;
							}
						}
					}
				}
			}
			num = d.size();
			if(num > k){
				distance = -100.0;
			}else{
				distance = d.get(num-1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return distance;
	}
}

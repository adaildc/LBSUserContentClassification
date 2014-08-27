package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import util.SQLInit;
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
		private String cclass;
		private ArrayList<Double> point;
		
		public IdPoint(){
			
		}
		
		public IdPoint(int id, String cclass, ArrayList<Double> point){
			this.id = id;
			this.cclass = cclass;
			this.point = point;
		}
		
		public int getId(){
			return id;
		}
		
		public String getCclass(){
			return this.cclass;
		}
		
		public ArrayList<Double> getPoint(){
			return this.point;
		}
	}
	
	private static class Distance{
		private double distance;
		private String cclass;
		
		public Distance(){
			
		}
		
		public Distance(double distance, String cclass){
			this.distance = distance;
			this.cclass = cclass;
		}
		
		public double getDistance(){
			return this.distance;
		}
		
		public String getCclass(){
			return this.cclass;
		}
	}
	
	public static void getClassThreshold(ResultSet rs, Connection conn, ArrayList<String> viawList, int k) throws Exception {
		String sql = "update lbs_sample set k_distance = ?, weight = ? where Id = ?";
		
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
			ArrayList<IdPoint> points = new ArrayList<>();
			while(it.hasNext()){
				cclass = it.next();
				ArrayList<IdTFIDF> details = hashmap.get(cclass);
				for(int i=0;i<details.size();i++){
					IdPoint idp = new IdPoint(details.get(i).getId(), cclass, Share.getVector(details.get(i).getTfidfMap(),viawList));
					points.add(idp);
				}
			}
			it = hashmap.keySet().iterator();
			while(it.hasNext()){
				//一次循环是对一个类别进行阈值的计算
				double[] para = new double[2];
				double distance = 0.0;		
				double weight = 0.0;
				cclass = it.next();
				IdPoint point = null;
				ArrayList<IdPoint> ps = new ArrayList<>();
				ArrayList<IdTFIDF> details = hashmap.get(cclass);
				for(int i=0;i<details.size();i++){
					IdPoint idp = new IdPoint(details.get(i).getId(), cclass, Share.getVector(details.get(i).getTfidfMap(),viawList));
					ps.add(idp);
				}
				Iterator<IdPoint> pit = ps.iterator();
				while(pit.hasNext()){
					point = pit.next();
					id = point.getId();
					para = getKNearestDistance(k, point.getPoint(), points, cclass);
					distance = para[0];
					weight = para[1];
					WriteToSQL.write(sql, conn, ""+distance, ""+weight, id);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static double[] getKNearestDistance(int k, ArrayList<Double> point, ArrayList<IdPoint> points, String cc) throws Exception {
		double[] para = new double[2];
		double distance = 0.0;
		double weight = 0.0;
		double n = 0.0;
		String cclass = "";
		
		try {
			ArrayList<Distance> d = new ArrayList<>(k);
			IdPoint p = null;
			Iterator<IdPoint> it = points.iterator();
			int num = 0;
			while(it.hasNext()){
				p = it.next();
				distance = Share.getDistance(point, p.getPoint());
				cclass = p.getCclass();
				Distance dist = new Distance(distance, cclass);
				num = d.size();
				if(num == 0){
					d.add(dist);
				}else{
					if(distance >= d.get(num-1).getDistance()){
						if(num < k){
							d.add(num, dist);
						}
					}else{
						for(int i=0;i<num;i++){
							if(distance <= d.get(i).getDistance()){
								d.add(i, dist);
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
				distance = d.get(num-1).getDistance();
				for(int i=0;i<num;i++){
					if(cc.equals(d.get(i).getCclass())){
						n++;
					}
				}
				weight = n/num;
			}
			para[0] = distance;
			para[1] = weight;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return para;
	}
}

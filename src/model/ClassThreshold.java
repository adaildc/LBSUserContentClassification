package model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import struct.ClassCenterThreshold;
import struct.VectorItemsAndWeight;

public class ClassThreshold {
	public static ArrayList<ClassCenterThreshold> getClassCenterThreshold(ResultSet rs, ArrayList<VectorItemsAndWeight> viawList) throws Exception {
		ArrayList<ClassCenterThreshold> centhr = null;
		ClassCenterThreshold cct = null;
		ArrayList<Double> center = new ArrayList<>();
		double threshold = 0.0;
		
		try {
			String cclass = "";
			String tfidf = "";
			HashMap<String, Double> tfidfmap = new HashMap<>();
			HashMap<String, ArrayList<HashMap<String, Double>>> hashmap = new HashMap<>();
			ArrayList<HashMap<String, Double>> details = new ArrayList<>();
			ArrayList<ArrayList<Double>> points = new ArrayList<>();
			double max = 0.0;
			double distance = 0.0;
			
			while(rs.next()){
				cclass = rs.getString("class");
				tfidf = rs.getString("tfidf");
				tfidfmap = TFIDF.strToMap(tfidf);
				
				if(hashmap.containsKey(cclass)){
					details = hashmap.get(cclass);
					details.add(tfidfmap);
					hashmap.put(cclass, details);
				}else{
					details.clear();
					details.add(tfidfmap);
					hashmap.put(cclass, details);
				}
			}
			
			Iterator<String> it = hashmap.keySet().iterator();
			while(it.hasNext()){
				//一次循环是对一个类别进行中心和半径的计算
				cclass = it.next();
				details = hashmap.get(cclass);
				for(int i=0;i<details.size();i++){
					points.add(getVector(details.get(i),viawList));
				}
				center = points.get(0);
				for(int i=1;i<points.size();i++){
					center = getCenter(center,points.get(i), viawList);
				}
				for(int i=0;i<points.size();i++){
					distance = getDistance(center, points.get(i));
					if(max < distance){
						max = distance;
					}
				}
				cct.setValues(cclass, center, max);
				centhr.add(cct);
			}
			////////////////////////上面只是半径，还不是阈值范围，接下来计算阈值范围
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return centhr;
	}
	
	public static ArrayList<Double> getVector(HashMap<String, Double> tfidf, ArrayList<VectorItemsAndWeight> viawList) throws Exception {
		ArrayList<Double> vector = new ArrayList<>();
		try {
			VectorItemsAndWeight viaw = null;
			String item = "";
			for(int i=0;i<viawList.size();i++){
				viaw = viawList.get(i);
				item = viaw.getItems();
				if(tfidf.containsKey(item)){
					vector.add(tfidf.get(item));
				}else{
					vector.add(0.0);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return vector;
	}
	
	public static ArrayList<Double> getCenter(ArrayList<Double> center, ArrayList<Double> point, ArrayList<VectorItemsAndWeight> viawList) throws Exception {
		ArrayList<Double> newCenter = new ArrayList<>();
		try {
			int num = viawList.size();
			double newtfidf = 0.0;
			for(int i=0;i<num;i++){
				newtfidf = viawList.get(i).getWeight() * (center.get(i) + point.get(i));
				newCenter.add(newtfidf);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newCenter;
	}
	
	public static double getDistance(ArrayList<Double> center, ArrayList<Double> point) throws Exception {
		double distance = 0.0;
		double sum = 0.0;
		
		try {
			if(center.size() != point.size()){
				return -100.0;
			}
			
			int num = center.size();
			for(int i=0;i<num;i++){
				sum = sum + Math.pow(center.get(i) - point.get(i),2);
			}
			distance = Math.sqrt(sum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return distance;
	}
	
}

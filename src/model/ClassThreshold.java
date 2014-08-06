package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import struct.ClassCenterThreshold;
import util.Share;

public class ClassThreshold {
	public static void getClassCenterThreshold(ResultSet rs, ArrayList<String> viawList) throws Exception {
		ArrayList<ClassCenterThreshold> centhr = new ArrayList<>();
		FileWriter writer = null;
		BufferedWriter bw = null;
		
		try {
			String cclass = "";
			String tfidf = "";
			String key = "";
			HashMap<String, Double> tfidfmap = new HashMap<>();
			HashMap<String, ArrayList<HashMap<String, Double>>> hashmap = new HashMap<>();
			
			while(rs.next()){
				cclass = rs.getString("class");
				tfidf = rs.getString("tfidf");
				tfidfmap = TFIDF.strToMap(tfidf);
				
				if(hashmap.containsKey(cclass)){
					ArrayList<HashMap<String, Double>> details = hashmap.get(cclass);
					details.add(tfidfmap);
					hashmap.put(cclass, details);
				}else{
					ArrayList<HashMap<String, Double>> details = new ArrayList<>();
					details.add(tfidfmap);
					hashmap.put(cclass, details);
				}
			}
			
			writer = new FileWriter("class.txt");
			bw = new BufferedWriter(writer);
			Iterator<String> it = hashmap.keySet().iterator();
			while(it.hasNext()){
				//一次循环是对一个类别进行中心和半径的计算
				double min = 0.0;
				double distance = 0.0;
				ArrayList<ArrayList<Double>> points = new ArrayList<>();
				cclass = it.next();
				ArrayList<HashMap<String, Double>> details = hashmap.get(cclass);
				for(int i=0;i<details.size();i++){
					points.add(Share.getVector(details.get(i),viawList));
				}
				ArrayList<Double> center = getCenter(points);
				
				Iterator<String> it1 = hashmap.keySet().iterator();
				ArrayList<Double> point = null;
				ArrayList<HashMap<String, Double>> details1 = null;
				while(it1.hasNext()){
					key = it1.next();
					if(!key.equals(cclass)){
						details1 = hashmap.get(cclass);
						for(int i=0;i<details1.size();i++){
							point = Share.getVector(details1.get(i),viawList);
							distance = Share.getDistance(center, point);
							if(min == 0.0 || distance < min){
								min = distance;
							}
						}
					}
				}
				bw.write( cclass + "    " + center + "    " +min + "\r\n");
				ClassCenterThreshold cct = new ClassCenterThreshold(cclass, center, min);
				centhr.add(cct);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			bw.close();
			writer.close();
		}
	}
	
	public static ArrayList<Double> getCenter(ArrayList<ArrayList<Double>> points) throws Exception {
		ArrayList<Double> sum = new ArrayList<>();
		try {
			int num = points.size();
			sum = points.get(0);
			int num1 = sum.size();
			for(int i=1;i<num;i++){
				for(int j=0;j<num1;j++){
					sum.set(j, sum.get(j)+points.get(i).get(j));
				}
			}
			for(int j=0;j<num1;j++){
				sum.set(j, sum.get(j)/num);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sum;
	}
}

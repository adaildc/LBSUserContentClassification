package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import struct.ClassCenterThreshold;

public class ClassThreshold {
	public static ArrayList<ClassCenterThreshold> getClassCenterThreshold(ResultSet rs, ArrayList<String> viawList) throws Exception {
		ArrayList<ClassCenterThreshold> centhr = new ArrayList<>();
		FileWriter writer = null;
		BufferedWriter bw = null;
		FileWriter writer1 = null;
		BufferedWriter bw1 = null;
		
		try {
			String cclass = "";
			String tfidf = "";
			HashMap<String, Double> tfidfmap = new HashMap<>();
			HashMap<String, ArrayList<HashMap<String, Double>>> hashmap = new HashMap<>();
			double distance = 0.0;
			
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
			
			Iterator<String> it = hashmap.keySet().iterator();
			while(it.hasNext()){
				//一次循环是对一个类别进行中心和半径的计算
				double max = 0.0;
				ArrayList<ArrayList<Double>> points = new ArrayList<>();
				cclass = it.next();
				ArrayList<HashMap<String, Double>> details = hashmap.get(cclass);
				for(int i=0;i<details.size();i++){
					points.add(getVector(details.get(i),viawList));
				}
				ArrayList<Double> center = getCenter(points);
				
				for(int i=0;i<points.size();i++){
					
					distance = getDistance(center, points.get(i));
					if(max < distance){
						max = distance;
					}
				}
				
				System.out.print(cclass+"  ");
				System.out.println("  "+max);
				
				centhr.add(new ClassCenterThreshold(cclass, center, 0.0, max));
			}
			System.out.println(" 类别中心和半径计算OK !");
			//上面只是半径，还不是阈值范围，接下来计算阈值范围
			int csize = centhr.size();
			double pdistance = 0.0;
			double ts = 0.0;
			writer = new FileWriter("class.txt");
			bw = new BufferedWriter(writer);
			
			for(int i=0;i<csize;i++){
				double threshold = -10.0;
				for(int j=0;j<csize;j++){
					if(threshold == 0.0){
						break;
					}
					if(j != i){
						pdistance = getDistance(centhr.get(i).getCenter(), centhr.get(j).getCenter());
						ts = pdistance - centhr.get(j).getMax();
						System.out.print(centhr.get(i).getCclass()+"和"+centhr.get(j).getCclass()+"的距离是：");
						System.out.println(pdistance);
						System.out.println("ts:"+ts);
						if(ts < 0.0){
							ts = 0.0;
						}
						if(threshold == -10.0 || threshold > ts){
							threshold = ts;
						}
					}
					System.out.println("threshold："+threshold);
				}
				
				ClassCenterThreshold cct = centhr.get(i);
				if(threshold < 0.0){
					threshold = 0.0;
				}
				cct.setThreshold(threshold);
				centhr.set(i,cct);
				ArrayList<Double> tcenter = cct.getCenter();
				System.out.println(cct.getCclass() + "         " + cct.getThreshold() + "\r\n");
				bw.write(cct.getCclass() + "    " + tcenter + "    " + cct.getThreshold() + "\r\n");
			}
			writer1 = new FileWriter("vectorItemsAndWeight.txt");
			bw1 = new BufferedWriter(writer1);
			for(int i=0;i<viawList.size();i++){
				bw1.write(viawList.get(i) + "    " + "\r\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			bw.close();
			writer.close();
			bw1.close();
			writer1.close();
		}
		
		return centhr;
	}
	
	public static ArrayList<Double> getVector(HashMap<String, Double> tfidf, ArrayList<String> viawList) throws Exception {
		ArrayList<Double> vector = new ArrayList<>();
		try {
			String item = "";
			for(int i=0;i<viawList.size();i++){
				item = viawList.get(i);
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

package util;

import java.util.ArrayList;
import java.util.HashMap;

public class Share {
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

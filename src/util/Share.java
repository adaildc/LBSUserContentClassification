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
}

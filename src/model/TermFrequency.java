package model;

import java.util.ArrayList;
import java.util.HashMap;

public class TermFrequency {
	public HashMap<String, Double> getTermFreq(ArrayList<String> list){
		HashMap<String, Double> map = new HashMap<>();
		try {
			String key = "";
			int length = list.size();
			for(int i=0;i<length;i++){
				key = list.get(i);
				if(map.containsKey(key)){
					map.put(key, map.get(key)+1.0);
				}else{
					map.put(key, 1.0);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map;
	}
}

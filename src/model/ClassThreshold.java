package model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import struct.ClassCenterThreshold;

public class ClassThreshold {
	public static ClassCenterThreshold[] getClassCenterThreshold(ResultSet rs) throws Exception {
		ClassCenterThreshold[] centhr = null;
		String cclass = "";
		String tfidf = "";
		HashMap<String, Double> tfidfmap = new HashMap<>();
		HashMap<String, ArrayList<HashMap<String, Double>>> hashmap = new HashMap<>();
		ArrayList<HashMap<String, Double>> details = new ArrayList<>();
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
			//下面添加一个计算中心点的函数和一个计算半径的函数
			//仿造之前写的聚类的计算中心点的代码
		}
		
		return centhr;
	}
}

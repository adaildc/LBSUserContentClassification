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
			//һ��ѭ���Ƕ�һ�����������ĺͰ뾶�ļ���
			//�������һ���������ĵ�ĺ�����һ������뾶�ĺ���
			//����֮ǰд�ľ���ļ������ĵ�Ĵ���
		}
		
		return centhr;
	}
}

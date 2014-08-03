package model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import struct.ClassCenterThreshold;
import struct.VectorItemsAndWeight;

public class ClassThreshold {
	public static ArrayList<ClassCenterThreshold> getClassCenterThreshold(ResultSet rs, ArrayList<VectorItemsAndWeight> viawList) throws Exception {
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
				//һ��ѭ���Ƕ�һ�����������ĺͰ뾶�ļ���
				double max = 0.0;
				ArrayList<ArrayList<Double>> points = new ArrayList<>();
				cclass = it.next();
				ArrayList<HashMap<String, Double>> details = hashmap.get(cclass);
				for(int i=0;i<details.size();i++){
					points.add(getVector(details.get(i),viawList));
				}
				ArrayList<Double> center = points.get(0);
				for(int i=1;i<points.size();i++){
					center = getCenter(center,points.get(i), viawList);
				}
				for(int i=0;i<points.size();i++){
					
					distance = getDistance(center, points.get(i));
					if(max < distance){
						max = distance;
					}
				}
				
				System.out.print(cclass+"  ");
				System.out.println("  "+max);
				
				centhr.add(new ClassCenterThreshold(cclass, center, max));
			}
			System.out.println(" ������ĺͰ뾶����OK !");
			//����ֻ�ǰ뾶����������ֵ��Χ��������������ֵ��Χ
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
						///////////�д����ڵ�һ�ּ���֮����ֵ���������ֵ�����ǳ��������ֵ
						//////////�ڶ��ּ���ᷢ������
						//////////�뾶̫�����ĵ�֮��ľ���̫С��������ֵΪ0�������ǲ���Ȩ�ص�����������
						//////////�������������⣬Ӧ����Ȩ�ؼ��㷽ʽ���⣬���磺
						///һ������һ�����ֶ�εĹؼ��ʿ�����������������ֵı���������
						///���԰���tfidf��˼·���㣺
						///�ڱ��������ֵĴ����ϸߣ��ڱ���������ֵĴ������٣����Ը��ϸߵ�Ȩ��
						///���磺A����a�ʳ��ֵ�Ƶ����aa/AA������aa���������A���г��ֵĴ�����AA��A���ܴ���
						///a���ڷ�A����ֵĴ�����!a����A���ܴ�����!A,���ʼȨ�ؿ�����(aa/AA)*(!A/!a)
						///��Ȩ�غܿ���>1�����ں��������ļ����У�Ӧ����Ȩ��֮��Ϊ1
						///���������3ά�ռ���������Ӧ�Ĵʷֱ�Ϊa,b,c
						///a,b,c�ĳ�ʼȨ�طֱ�Ϊcs_a,cs_b,cs_c(���Ƕ�����1)
						///������˴�������(����)Ȩ�أ�
						///a��Ȩ����cs_a/(cs_a+cs_b+cs_c)
						///b��Ȩ����cs_b/(cs_a+cs_b+cs_c)
						///c��Ȩ����cs_c/(cs_a+cs_b+cs_c)
						///���ǵĺ�Ϊ1
						///������������ݵ�ʱ��������Էִ��������ķִ������ҵ��������ҵ���Ȩ�ؼ���
						///����Ҳ�����ȥ������ʣ���Ϊ����ֻ�����࣬������Ҫ���������Ų�
						pdistance = getDistance(centhr.get(i).getCenter(), centhr.get(j).getCenter());
						ts = pdistance - centhr.get(j).getThreshold();
						System.out.print(centhr.get(i).getCclass()+"��"+centhr.get(j).getCclass()+"�ľ����ǣ�");
						System.out.println(pdistance);
						System.out.println("ts:"+ts);
						if(ts < 0.0){
							ts = 0.0;
						}
						if(threshold == -10.0 || threshold > ts){
							threshold = ts;
						}
					}
					System.out.println("threshold��"+threshold);
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
				bw1.write(viawList.get(i).getItem() + "    " + viawList.get(i).getWeight() + "\r\n");
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
	
	public static ArrayList<Double> getVector(HashMap<String, Double> tfidf, ArrayList<VectorItemsAndWeight> viawList) throws Exception {
		ArrayList<Double> vector = new ArrayList<>();
		try {
			VectorItemsAndWeight viaw = null;
			String item = "";
			for(int i=0;i<viawList.size();i++){
				viaw = viawList.get(i);
				item = viaw.getItem();
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

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
				//一次循环是对一个类别进行中心和半径的计算
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
						///////////有错误，在第一轮计算之后，阈值不再是最大值，而是成了真的阈值
						//////////第二轮计算会发生错误
						//////////半径太大，中心点之间的距离太小，导致阈值为0，看看是不是权重的设置有问题
						//////////或者是其他问题，应该是权重计算方式问题，比如：
						///一个类别的一个出现多次的关键词可能在所有样本里出现的比例并不高
						///可以案遭tfidf的思路计算：
						///在本类别里出现的次数较高，在别的类别里出现的次数较少，可以给较高的权重
						///例如：A类里a词出现的频率是aa/AA，其中aa是这个词在A类中出现的次数，AA是A类总词数
						///a词在非A类出现的次数是!a，非A类总词数是!A,则初始权重可以是(aa/AA)*(!A/!a)
						///该权重很可能>1，但在后续的中心计算中，应当让权重之和为1
						///假设最后是3维空间向量，对应的词分别为a,b,c
						///a,b,c的初始权重分别为cs_a,cs_b,cs_c(它们都大于1)
						///可做如此处理，计算(最终)权重：
						///a的权重是cs_a/(cs_a+cs_b+cs_c)
						///b的权重是cs_b/(cs_a+cs_b+cs_c)
						///c的权重是cs_c/(cs_a+cs_b+cs_c)
						///它们的和为1
						///当引入测试数据的时候，如果测试分词在样本的分词中能找到，则按照找到的权重即可
						///如果找不到，去掉这个词，因为这里只做分类，后续还要做独异点的排查
						pdistance = getDistance(centhr.get(i).getCenter(), centhr.get(j).getCenter());
						ts = pdistance - centhr.get(j).getThreshold();
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

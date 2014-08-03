package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import struct.VectorItemsAndWeight;
import util.ReadFromSQL;
import util.SQLInit;
import util.WriteToSQL;

public class TFIDF {
	public static HashMap<String, Double> getTF(HashMap<String, Double> map, double num) throws Exception{
		HashMap<String, Double> tf = new HashMap<>();
		try {
			String key = "";
			Set keySet = map.keySet();
			Iterator<String> it = keySet.iterator();
			while(it.hasNext()){//tf
				key = it.next();
				tf.put(key, map.get(key)/num);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tf;
	}
	
	public static String mapToStr(HashMap<String, Double> map) throws Exception{
		String str = "";
		try {
			String key = "";
			Iterator<String> it = map.keySet().iterator();
			while(it.hasNext()){
				key = it.next();
				str = str + ";" + key + ":" + map.get(key);
			}
			str = str.substring(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	
	public static HashMap<String, Double> strToMap(String str) throws Exception{
		HashMap<String, Double> hashmap = new HashMap<>();
		try {
			String[] kvs = str.split(";");
			String[] kv = new String[2];
			for(int i=0;i<kvs.length;i++){
				kv = kvs[i].split(":");
				hashmap.put(kv[0], Double.parseDouble(kv[1]));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hashmap;
	}
	
	public static HashMap<String, Double> tf(ResultSet rs) throws Exception{
		int id = 0;
		String content = "";
		ArrayList<String> list = new ArrayList<>();
		int num = 0;
		String key = "";
		HashMap<String,Double> map = new HashMap<>();
		HashMap<String,Double> tfmap = new HashMap<>();
		try{
			id = rs.getInt("Id");
			content = rs.getString("user_content");
			Analyzer analyzer = new PaodingAnalyzer();
			StringReader reader = new StringReader(content);
			TokenStream ts = analyzer.tokenStream(content, reader);
			list = Participle.displayTokenStream(ts);
			num = list.size();
			for(int i=0;i<num;i++){
				key = list.get(i);
				if(map.containsKey(key)){
					map.put(key, map.get(key)+1.0);
				}else{
					map.put(key, 1.0);
				}
			}
			tfmap = getTF(map, num);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tfmap;
	}
	
	public static ArrayList<VectorItemsAndWeight> toTFtoIDF() throws Exception{
		//将所有记录(或者称为文件)转化成hashset形式，一个记录对应一个无重复分词的[分词_1,分词_2,...,分词_n]
		//对所有转化后的文件进行分词统计，即整个语料库包含的无重复分词，一个记录指针i，记录一个分词在多少个文件中出现
		//上面的过程可以通过HashMap<String,Integer>来进行统计，正是之前做过的
		//写入到idf文本文件
		
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		FileWriter writer = null;
		BufferedWriter bw = null;
		ArrayList<VectorItemsAndWeight> vectorItemsAndWeightList = new ArrayList<>();
		try {
			int total_num = 0;
			double num = 0.0;
			double sum = 0.0;
			int id = 0;
			Double idf = 0.0;
			String sql = "select * from lbs_sample";
			String sql1 = "update lbs_sample set tf = ? where Id = ?";
			String key = "";
			String content = "";
			ArrayList<String> list = new ArrayList<>();
			HashMap<String,Double> map = new HashMap<>();
			HashMap<String,Double> summap = new HashMap<>();
			HashMap<String,Double> hashmap = new HashMap<>();
			HashMap<String,Double> tfmap = new HashMap<>();
			String tfstr = "";
			
			SQLInit.initParam("mysql.properties");
			conn = SQLInit.getConn();
			rs = ReadFromSQL.query(sql,conn);
			while(rs.next()){
				id = rs.getInt("Id");
				content = rs.getString("user_content");
				Analyzer analyzer = new PaodingAnalyzer();
				StringReader reader = new StringReader(content);
				TokenStream ts = analyzer.tokenStream(content, reader);
				list = Participle.displayTokenStream(ts);
				num = list.size();
				sum = sum + num;
				for(int i=0;i<num;i++){
					key = list.get(i);
					if(map.containsKey(key)){
						map.put(key, map.get(key)+1.0);
					}else{
						map.put(key, 1.0);
					}
					if(summap.containsKey(key)){
						summap.put(key, summap.get(key)+1.0);
					}else{
						summap.put(key, 1.0);
					}
				}
				
				tfmap = getTF(map, num);
				tfstr = mapToStr(tfmap);
				WriteToSQL.write(sql1, conn, tfstr, id);
				
				Iterator<String> it = map.keySet().iterator();
				while(it.hasNext()){
					key = it.next();
					if(hashmap.containsKey(key)){
						hashmap.put(key, hashmap.get(key)+1.0);
					}else{
						hashmap.put(key, 1.0);
					}
				}
				map.clear();
			}
			Iterator<String> viawit = summap.keySet().iterator();//这里做中心点权重很有问题
			while(viawit.hasNext()){
				key = viawit.next();
				vectorItemsAndWeightList.add(new VectorItemsAndWeight(key, summap.get(key)/sum));
			}
			
			rs.last();
			total_num = rs.getRow();
			
			writer = new FileWriter("idf.txt");//加一个写文本操作，写到idf文本
			bw = new BufferedWriter(writer);
			
			Iterator<String> new_it = hashmap.keySet().iterator();
			while(new_it.hasNext()){
				key = new_it.next();
				idf = Math.log(total_num/(1.0+hashmap.get(key)));
				hashmap.put(key, idf);
				bw.write(key + "    "+ idf+"\r\n");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			SQLInit.closeSQL(conn, rs, stmt);
			bw.close();
			writer.close();
		}
		return vectorItemsAndWeightList;
	}
	
	public static HashMap<String, Double> getIDF() throws Exception{
		HashMap<String, Double> idf = new HashMap<>();
		BufferedReader br = null;
		try {
			String str = "";
			String[] s = new String[2];
			br = new BufferedReader(new FileReader("idf.txt"));
			while((str = br.readLine())!=null){
				s = str.split("    ");
				idf.put(s[0], Double.parseDouble(s[1]));	
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			br.close();
		}
		return idf;
	}
	
	//idf去idf文件查询
	public static HashMap<String, Double> getTFIDF(HashMap<String, Double> map
			,HashMap<String, Double> idf, int total_num) throws Exception{
		HashMap<String, Double> tf_idf = new HashMap<>();
		
		try {
			String key = "";
			double num = 0.0;
			double idf_flag = 0.0;
			Iterator<String> it = map.keySet().iterator();
			while(it.hasNext()){
				key = it.next();
				num += map.get(key);
			}
			tf_idf = getTF(map, num);
			it = tf_idf.keySet().iterator();
			while(it.hasNext()){
				key = it.next();
				if(tf_idf.containsKey(key)){
					if(idf.containsKey(key)){
						idf_flag = idf.get(key);
					}else{
						idf_flag = Math.log(total_num);
					}
					tf_idf.put(key, tf_idf.get(key)*idf_flag);
				}else{
					tf_idf.put(key, 0.0);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tf_idf;
	}
	
	public static HashMap<String,Double> tfidf(ResultSet rs, HashMap<String,Double> tfmap, int total_num) throws Exception{
		int id = 0;
		String tf;
		HashMap<String,Double> tfidfmap = new HashMap<>();
		HashMap<String,Double> idfmap = new HashMap<>();
		
		try{
			idfmap = getIDF();
			tfidfmap = getTFIDF(tfmap, idfmap, total_num);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tfidfmap;
	}
	
	public static void toTFIDF(int total_num) throws Exception{
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			int id = 0;
			String sql = "select * from lbs_sample";
			String sql1 = "update lbs_sample set tfidf = ? where Id = ?";
			String tf = "";
			HashMap<String,Double> tfidfmap = new HashMap<>();
			HashMap<String,Double> idfmap = new HashMap<>();
			HashMap<String,Double> tfmap = new HashMap<>();
			String tfidfstr = "";
			stmt = null;
			
			SQLInit.initParam("mysql.properties");
			conn = SQLInit.getConn();
			rs = ReadFromSQL.query(sql,conn);
			while(rs.next()){
				id = rs.getInt("Id");
				tf = rs.getString("tf");
				tfmap = strToMap(tf);
				idfmap = getIDF();
				tfidfmap = getTFIDF(tfmap, idfmap, total_num);
				tfidfstr = mapToStr(tfidfmap);
				WriteToSQL.write(sql1, conn, tfidfstr, id);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			SQLInit.closeSQL(conn, rs, stmt);
		}
	}
	
	public static void main(String[] args) throws Exception {
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		int total_num = 0;
		try {
			String sql = "select count(*) from lbs_sample";
			SQLInit.initParam("mysql.properties");
			conn = SQLInit.getConn();
			rs = ReadFromSQL.query(sql,conn);
			rs.next();
			total_num = rs.getInt(1);
			System.out.println(total_num);
			toTFtoIDF();
			toTFIDF(total_num);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			SQLInit.closeSQL(conn, rs, stmt);
		}
	}
}

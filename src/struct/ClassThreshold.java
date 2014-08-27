package struct;

import java.util.ArrayList;

public class ClassThreshold {
	private String cclass;
	private ArrayList<Double> point;
	private double threshold;
	private double weight;
	
	public ClassThreshold(){
		
	}
	
	public ClassThreshold(String cclass, ArrayList<Double> point, double threshold, double weight){
		this.cclass = cclass;
		this.point = point;
		this.threshold = threshold;
		this.weight = weight;
	}
	
	public String getCclass(){
		return this.cclass;
	}
	
	public ArrayList<Double> getPoint(){
		return this.point;
	}
	
	public double getThreshold(){
		return this.threshold;
	}
	
	public double getWeight(){
		return this.weight;
	}
}

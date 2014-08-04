package struct;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassCenterThreshold {
	private String cclass;
	private ArrayList<Double> center;
	private double threshold;
	private double max;
	
	public ClassCenterThreshold(){
		
	}
	
	public ClassCenterThreshold(String cclass, ArrayList<Double> center, double threshold, double max){
		this.cclass = cclass;
		this.center = center;
		this.threshold = threshold;
		this.max = max;
	}
	
	public String getCclass(){
		return this.cclass;
	}
	
	public ArrayList<Double> getCenter(){
		return this.center;
	}
	
	public double getThreshold(){
		return this.threshold;
	}
	
	public double getMax(){
		return this.max;
	}
	
	public void setValues(String cclass, ArrayList<Double> center, double threshold, double max){
		this.cclass = cclass;
		this.center = center;
		this.threshold = threshold;
		this.max = max;
	}
	
	public void setThreshold(double threshold){
		this.threshold = threshold;
	}
}

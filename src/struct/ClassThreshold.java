package struct;

import java.util.ArrayList;

public class ClassThreshold {
	private String cclass;
	private ArrayList<Double> center;
	private double threshold;
	
	public ClassThreshold(){
		
	}
	
	public ClassThreshold(String cclass, ArrayList<Double> center, double threshold){
		this.cclass = cclass;
		this.center = center;
		this.threshold = threshold;
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
	
	public void setValues(String cclass, ArrayList<Double> center, double threshold){
		this.cclass = cclass;
		this.center = center;
		this.threshold = threshold;
	}
	
	public void setThreshold(double threshold){
		this.threshold = threshold;
	}
}

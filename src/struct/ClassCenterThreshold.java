package struct;

import java.util.HashMap;

public class ClassCenterThreshold {
	private String cclass;
	private HashMap<String, Double> center;
	private double threshold;
	
	public String getCclass(){
		return this.cclass;
	}
	
	public HashMap<String, Double> getCenter(){
		return this.center;
	}
	
	public double getThreshold(){
		return this.threshold;
	}
}

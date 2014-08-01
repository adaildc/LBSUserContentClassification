package struct;

public class VectorItemsAndWeight {
	private String item;
	private double weight;
	
	public VectorItemsAndWeight(){
		
	}
	
	public VectorItemsAndWeight(String item, double weight){
		this.item = item;
		this.weight = weight;
	}
	
	public String getItems() {
		return this.item;
	}
	
	public double getWeight() {
		return this.weight;
	}
	
	public boolean equals(Object obj) { 
		if(getClass() != obj.getClass() )  
	        return false;
		VectorItemsAndWeight vectorItemsAndWeight = (VectorItemsAndWeight) obj;
		if(this.item.equals(vectorItemsAndWeight.item)){
			return true;
		}else{
			return false;
		}
	}
}

package struct;

import java.util.Date;

public class LBSClass {
	private int id;
	private int user_id;
	private String user_content;
	private Date date;
	private String user_lbs;
	
	public int getId(){
		return this.id;
	}
	
	public int getUser_id(){
		return this.user_id;
	}
	
	public String getUser_content(){
		return this.user_content;
	}
	
	public Date getDate(){
		return this.date;
	}
	
	public String getUser_lbs(){
		return this.user_lbs;
	}
}

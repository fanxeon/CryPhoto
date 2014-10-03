package com.example.database;

public class SpinnerNavItem {

	private String title;
	private int icon;
	
	public SpinnerNavItem(String title, int icon){
		this.title = title;
		this.icon = icon;
	}
	
	public SpinnerNavItem(String title){
		this.title = title;
	}
	
	public String getTitle(){
		return this.title;		
	}
	
	public int getIcon(){
		return this.icon;
	}
}

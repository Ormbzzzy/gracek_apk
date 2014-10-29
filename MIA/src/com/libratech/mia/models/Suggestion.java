package com.libratech.mia.models;

public class Suggestion {
	String title;
	String comment;
	String date;
	
	public Suggestion(String title, String comment, String date){
		this.title = title;
		this.comment = comment;
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

}

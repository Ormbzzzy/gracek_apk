package com.libratech.mia.models;

public class User {

	String id;
	String fName;
	String lName;

	public User(String id, String fName, String lName) {
		this.fName = fName;
		this.lName = lName;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getfName() {
		return fName;
	}

	public String getlName() {
		return lName;
	}

}

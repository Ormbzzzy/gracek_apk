package com.libratech.mia.models;

public class User {

	String id;
	String fName;
	String lName;
	String role;

	public User(String id, String fName, String lName, String role) {
		this.fName = fName;
		this.lName = lName;
		this.id = id;
		this.role = role;
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

	public String getRole() {
		return role;
	}
}

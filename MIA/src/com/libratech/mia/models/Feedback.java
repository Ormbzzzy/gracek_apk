package com.libratech.mia.models;

public class Feedback {

	String name;
	String brand;
	String upc;
	String date;
	boolean urgent = false;

	public Feedback(String name, String brand, String upc, String date,
			String urg) {
		this.name = name;
		this.brand = brand;
		this.upc = upc;
		this.date = date;
		if (urg.equals("yes"))
			urgent = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isUrgent() {
		return urgent;
	}

	public void setUrgent(boolean urgent) {
		this.urgent = urgent;
	}

}

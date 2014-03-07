package com.libratech.mia.models;
//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : Employee.java
//  @ Date : 1/12/2014
//  @ Author : 
//
//




public abstract class Employee
{
	private String empID;
	private String firstName;
	private String lastName;
	private String dob;
	private String streetNo;
	private String streetName;
	private String city;
	private String phoneNumber;
	
	public Employee(String empID, String firstName, String lastName,
			String dob, String streetNo, String streetName, String city,
			String phoneNumber) 
	{		
		this.empID = empID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.streetNo = streetNo;
		this.streetName = streetName;
		this.city = city;
		this.phoneNumber = phoneNumber;
	}

	public String getEmpID() {
		return empID;
	}

	public void setEmpID(String empID) {
		this.empID = empID;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getStreetNo() {
		return streetNo;
	}

	public void setStreetNo(String streetNo) {
		this.streetNo = streetNo;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
}
package com.libratech.mia.models;
//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : Scanned.java
//  @ Date : 1/12/2014
//  @ Author : 
//
//




public class Scanned extends Product 
{
	boolean scanned;
	public Scanned(String upcCode, String weight, String productName,
			String description, String brand, String category, String uom,
			float price, String gct, String photo,boolean scanned)
	{
		super(upcCode, weight, productName, description, brand, category, uom, price,
				gct, photo);
		this.scanned = scanned;
		// TODO Auto-generated constructor stub
	}
	public Scanned(Product p,boolean scanned)
	{
		super(p.getUpcCode(), p.getWeight(), p.getProductName(), p.getDescription(), p.getBrand(), p.getCategory(), p.getUom(), p.getPrice(),
				p.getGct(), p.getPhoto());
		this.scanned = scanned;
		
	}
	public boolean getScanned()
	{
		return scanned;
	}
}

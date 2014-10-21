package com.libratech.mia.models;

import java.util.Comparator;

//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : Product.java
//  @ Date : 1/12/2014
//  @ Author : 
//
//

public class Product {
	private String upcCode;
	private String weight;
	private String productName;
	private String description;
	private String brand;
	private String category;
	private String uom;
	private float price = (float) 0.00;
	private String gct;
	private String photo;

	public Product(String upcCode, String weight, String productName,
			String description, String brand, String category, String uom,
			float price, String gct, String photo) {
		this.upcCode = upcCode;
		this.weight = weight;
		this.productName = productName;
		this.description = description;
		this.brand = brand;
		this.category = category;
		this.uom = uom;
		this.price = price;
		this.gct = gct;
		this.photo = photo;
	}

	public Product() {
		// TODO Auto-generated constructor stub
		this.upcCode = this.weight = this.productName = this.description = this.brand = this.category = this.uom = this.gct = this.photo = "";
		this.price = (float) 0.00;
	}

	public String getUpcCode() {
		return upcCode;
	}

	public void setUpcCode(String upcCode) {
		this.upcCode = upcCode;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getGct() {
		return gct;
	}

	public void setGct(String gct) {
		this.gct = gct;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String toString() {
		return upcCode + " " + productName + " " + brand + " " + weight + " "
				+ uom;
	}

	public static Comparator<Product> ascCatComparator = new Comparator<Product>() {

		public int compare(Product p1, Product p2) {

			String pCat1 = p1.getCategory().toUpperCase();
			String pCat2 = p2.getCategory().toUpperCase();

			return pCat1.compareTo(pCat2);

		};

	};
	public static Comparator<Product> desCatComparator = new Comparator<Product>() {

		public int compare(Product p1, Product p2) {

			String pCat1 = p1.getCategory().toUpperCase();
			String pCat2 = p2.getCategory().toUpperCase();

			return pCat2.compareTo(pCat1);
		};
	};

	public static Comparator<Product> ascNameComparator = new Comparator<Product>() {

		public int compare(Product p1, Product p2) {

			String pCat1 = p1.getProductName().toUpperCase();
			String pCat2 = p2.getProductName().toUpperCase();

			return pCat1.compareTo(pCat2);

		};

	};
	public static Comparator<Product> desNameComparator = new Comparator<Product>() {

		public int compare(Product p1, Product p2) {

			String pCat1 = p1.getProductName().toUpperCase();
			String pCat2 = p2.getProductName().toUpperCase();

			return pCat2.compareTo(pCat1);
		};
	};

	public static Comparator<Product> ascBrandComparator = new Comparator<Product>() {

		public int compare(Product p1, Product p2) {

			String pCat1 = p1.getBrand().toUpperCase();
			String pCat2 = p2.getBrand().toUpperCase();

			return pCat1.compareTo(pCat2);

		};

	};
	public static Comparator<Product> desBrandComparator = new Comparator<Product>() {

		public int compare(Product p1, Product p2) {

			String pCat1 = p1.getBrand().toUpperCase();
			String pCat2 = p2.getBrand().toUpperCase();

			return pCat2.compareTo(pCat1);
		};
	};
}
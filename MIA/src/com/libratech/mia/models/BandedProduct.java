package com.libratech.mia.models;
import java.util.ArrayList;

//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : BandedProduct.java
//  @ Date : 1/12/2014
//  @ Author : 
//
//


public class BandedProduct 
{
	private float totalPrice;
	private ArrayList <Product> products;
	
	public BandedProduct(float totalPrice, ArrayList<Product> products) 
	{		
		this.totalPrice = totalPrice;
		this.products = products;
	}

	public float gettotalPrice() {
		return totalPrice;
	}

	public void settotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public ArrayList<Product> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}

}
//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : InStoreSample.java
//  @ Date : 1/12/2014
//  @ Author : 
//
//




public class InStoreSample extends Product 
{
	private String comments;

	public InStoreSample(int upcCode, int weight, String productName,
			String description, String brand, String category, String uom,
			float price, String gct, String photo, String comments) 
	{
		super(upcCode, weight, productName, description, brand, category, uom,
				price, gct, photo);
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
}

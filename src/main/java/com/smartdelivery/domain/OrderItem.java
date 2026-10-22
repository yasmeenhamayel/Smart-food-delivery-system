package com.smartdelivery.domain;

public class OrderItem {
	private MenuItem menuItem;
	private int quantity;
	
	public OrderItem (MenuItem menuItem, int quantity){
		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity must be positive");
		}
		if (menuItem == null) {
			throw new IllegalArgumentException("MenuItem must not be null");
		}
		if (!menuItem.isAvailable()) {
		    throw new IllegalArgumentException("This MenuItem is not Available");

		}
		this.quantity = quantity;
		this.menuItem = menuItem;
	}
	 
	
	public MenuItem getMenuItem() {
		return menuItem;
	}
	
	
	public int getQuantity() {
		return quantity;
	}
	
	public double getSubtotal(){
		return quantity* menuItem.getPrice();
	}
	

}

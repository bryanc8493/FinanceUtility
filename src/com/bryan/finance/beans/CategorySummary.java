package com.bryan.finance.beans;

public class CategorySummary {

	private String category;
	private double amount;
	
	public CategorySummary(String category, double amt) {
		this.category = category;
		this.amount = amt;
	}
	
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
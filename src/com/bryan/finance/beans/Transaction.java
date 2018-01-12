package com.bryan.finance.beans;

import java.io.Serializable;

public class Transaction implements Serializable {

	private static final long serialVersionUID = -4582287006011515009L;

	public Transaction() {}

	private String transactionID;
	private String title;
	private String type;
	private String category;
	private String date;
	private String store;
	private String amount;
	private String combinedAmount;
	private String description;
	private boolean savings;
	private char credit;
	private char creditPaid;
	
	/**
	 * @return the transactionID
	 */
	public String getTransactionID() {
		return transactionID;
	}
	/**
	 * @param transactionID the transactionID to set
	 */
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the store
	 */
	public String getStore() {
		return store;
	}
	/**
	 * @param store the store to set
	 */
	public void setStore(String store) {
		this.store = store;
	}
	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}
	/**
	 * @return the combinedAmount
	 */
	public String getCombinedAmount() {
		return combinedAmount;
	}
	/**
	 * @param combinedAmount the combinedAmount to set
	 */
	public void setCombinedAmount(String combinedAmount) {
		this.combinedAmount = combinedAmount;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the savings
	 */
	public boolean isSavings() {
		return savings;
	}
	/**
	 * @param savings the savings to set
	 */
	public void setSavings(boolean savings) {
		this.savings = savings;
	}
	/**
	 * @return the credit
	 */
	public char getCredit() {
		return credit;
	}
	/**
	 * @param credit the credit to set
	 */
	public void setCredit(char credit) {
		this.credit = credit;
	}
	/**
	 * @return the creditPaid
	 */
	public char getCreditPaid() {
		return creditPaid;
	}
	/**
	 * @param creditPaid the creditPaid to set
	 */
	public void setCreditPaid(char creditPaid) {
		this.creditPaid = creditPaid;
	}
	
}

package com.bryan.finance.beans;

import java.text.DecimalFormat;

public class MonthlyRecord {

	private static final DecimalFormat df = new DecimalFormat("#.##");
	
	public MonthlyRecord() {
		
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(int month) {
		if(month == 1) {
			this.month = "January";
		}else if(month == 2) {
			this.month = "February";
		}else if(month == 3) {
			this.month = "March";
		}else if(month == 4) {
			this.month = "April";
		}else if(month == 5) {
			this.month = "May";
		}else if(month == 6) {
			this.month = "June";
		}else if(month == 7) {
			this.month = "July";
		}else if(month == 8) {
			this.month = "August";
		}else if(month == 9) {
			this.month = "September";
		}else if(month == 10) {
			this.month = "October";
		}else if(month == 11) {
			this.month = "November";
		}else if(month == 12) {
			this.month = "December";
		}
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the expenses
	 */
	public double getExpenses() {
		return expenses;
	}

	/**
	 * @param expenses the expenses to set
	 */
	public void setExpenses(double expenses) {
		this.expenses = expenses;
	}

	/**
	 * @return the income
	 */
	public double getIncome() {
		return income;
	}

	/**
	 * @param income the income to set
	 */
	public void setIncome(double income) {
		this.income = income;
	}

	/**
	 * @return the cashFlow
	 */
	public double getCashFlow() {
		return cashFlow;
	}

	/**
	 * @param cashFlow the cashFlow to set
	 */
	public void setCashFlow(double income, double expenses) {
		String temp = df.format(income - expenses);
		this.cashFlow = Double.parseDouble(temp);
	}
	
	@Override
	public String toString() {
		return "ID:\t"+ this.id +
				"\nMonth:\t"+ this.month + 
				"\nYear:\t"+ this.year +
				"\nExpenses:\t" + this.expenses +
				"\nIncome:\t" + this.income +
				"\nCash Flow:\t" + this.cashFlow;
	}

	private int id;
	private String month;
	private int year;
	private double expenses;
	private double income;
	private double cashFlow;

}

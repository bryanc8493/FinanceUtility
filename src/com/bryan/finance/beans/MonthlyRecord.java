package com.bryan.finance.beans;

import java.text.DecimalFormat;

public class MonthlyRecord {

	private static final DecimalFormat df = new DecimalFormat("#.##");
	
	public MonthlyRecord() {
		
	}

	public String getMonth() {
		return month;
	}

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

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public double getExpenses() {
		return expenses;
	}

	public void setExpenses(double expenses) {
		this.expenses = expenses;
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

	public double getCashFlow() {
		return cashFlow;
	}

	public void setCashFlow(double income, double expenses) {
		String temp = df.format(income - expenses);
		this.cashFlow = Double.parseDouble(temp);
	}

	public int getMonthInt() {
		return monthInt;
	}

	public void setMonthInt(int monthInt) {
		this.monthInt = monthInt;
	}

	private String month;
	private int monthInt;
	private int year;
	private double expenses;
	private double income;
	private double cashFlow;

}

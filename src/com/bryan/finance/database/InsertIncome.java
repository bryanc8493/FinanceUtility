package com.bryan.finance.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.bryan.finance.beans.Transaction;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;

public class InsertIncome {

	private static Logger logger = Logger.getLogger(InsertIncome.class);

	public static void NewIncome(Transaction tran, final Connection con) {
		logger.debug("Inserting Income Record");
		int recordsInserted1 = 0;
		int recordsInserted2 = 0;
		int recordsInserted3 = 0;
		PreparedStatement ps;
		String SQL_TEXT = ("INSERT INTO " + Tables.INCOME
				+ " (TITLE, CATEGORY, TRANSACTION_DATE, AMOUNT, DESCRIPTION)"
				+ " Values(\"" + tran.getTitle() + "\", \""
				+ tran.getCategory() + "\", \"" + tran.getDate() + "\", \""
				+ tran.getAmount() + "\", \"" + tran.getDescription() + "\");");
		try {
			ps = con.prepareStatement(SQL_TEXT);
			logger.debug("Income Table Query: " + SQL_TEXT);
			recordsInserted1 = ps.executeUpdate();
		} catch (SQLException e) {
			throw new AppException(e);
		}

		/*
		 * Query to insert same data into the monthly transactions table
		 */
		SQL_TEXT = ("INSERT INTO "
				+ Tables.MONTHLY_TRANSACTIONS
				+ " (TITLE, TYPE, CATEGORY, TRANSACTION_DATE, AMOUNT, "
				+ "COMBINED_AMOUNT, DESCRIPTION, CREDIT, CREDIT_PAID) Values(\""
				+ tran.getTitle() + "\", \"" + tran.getType() + "\", \""
				+ tran.getCategory() + "\", \"" + tran.getDate() + "\", "
				+ "\"" + tran.getAmount() + "\", \"" + tran.getCombinedAmount()
				+ "\", \"" + tran.getDescription() + "\", \"0\", \" \");");
		try {
			ps = con.prepareStatement(SQL_TEXT);
			logger.debug("Monthly Table Query: " + SQL_TEXT);
			recordsInserted2 = ps.executeUpdate();
		} catch (SQLException e) {
			throw new AppException(e);
		}

		/*
		 * Check if the income category was from the savings account, if yes, we
		 * need to add that data to the savings table
		 */
		if (tran.getCategory().trim().equalsIgnoreCase("Savings Transfer")) {
			SQL_TEXT = "INSERT INTO "
					+ Tables.SAVINGS
					+ " (TRANS_TYPE, TRANS_DATE, AMOUNT, DESCRIPTION, SUM_AMOUNT) "
					+ "Values(\"Expense\", \"" + tran.getDate() + "\", \""
					+ tran.getAmount() + "\", \"" + tran.getDescription()
					+ "\", \"-" + tran.getAmount() + "\");";
			try {
				ps = con.prepareStatement(SQL_TEXT);
				recordsInserted3 = ps.executeUpdate();
			} catch (SQLException e) {
				throw new AppException(e);
			}
		}

		int totalRecordsInserted = recordsInserted1 + recordsInserted2
				+ recordsInserted3;
		logger.info("Success, inserted " + totalRecordsInserted + " records");

		try {
			ps.close();
		} catch (SQLException e) {
			throw new AppException(e);
		}
	}
}

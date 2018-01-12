package com.bryan.finance.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.bryan.finance.beans.Transaction;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;

public class InsertExpense {

	private static Logger logger = Logger.getLogger(InsertExpense.class);
	private static PreparedStatement ps;

	public static void NewExpense(Transaction tran, final Connection con) {
		logger.debug("Inserting expense record");
		int recordsInserted1 = 0;
		int recordsInserted2 = 0;
		int recordsInserted3 = 0;

		String SQL_TEXT = ("INSERT INTO " + Tables.EXPENSES
				+ " (TITLE, CATEGORY, TRANSACTION_DATE, AMOUNT,"
				+ " DESCRIPTION, STORE) Values(\"" + tran.getTitle() + "\", \""
				+ tran.getCategory() + "\", \"" + tran.getDate() + "\", \""
				+ tran.getAmount() + "\", \"" + tran.getDescription()
				+ "\", \"" + tran.getStore() + "\");");
		try {
			ps = con.prepareStatement(SQL_TEXT);
			logger.debug("Expense Table Query: " + SQL_TEXT);
			recordsInserted1 = ps.executeUpdate();
		} catch (SQLException e) {
			throw new AppException(e);
		}

		SQL_TEXT = ("INSERT INTO "
				+ Tables.MONTHLY_TRANSACTIONS
				+ " (TITLE, TYPE, CATEGORY, TRANSACTION_DATE, AMOUNT, "
				+ "COMBINED_AMOUNT, DESCRIPTION, CREDIT, CREDIT_PAID) Values(\""
				+ tran.getTitle() + "\", \"" + tran.getType() + "\", \""
				+ tran.getCategory() + "\", \"" + tran.getDate() + "\", "
				+ "\"" + tran.getAmount() + "\", \"" + tran.getCombinedAmount()
				+ "\", \"" + tran.getDescription() + "\", " + "\""
				+ tran.getCredit() + "\", \"" + tran.getCreditPaid() + "\");");
		try {
			ps = con.prepareStatement(SQL_TEXT);
			logger.debug("Monthly Table Query...\n" + SQL_TEXT);
			recordsInserted2 = ps.executeUpdate();
		} catch (SQLException e) {
			throw new AppException(e);
		}

		/*
		 * Check if the income category was from the savings account, if yes, we
		 * need to add that data to the savings table
		 */
		if (tran.getCategory().trim().equalsIgnoreCase("Savings")) {
			SQL_TEXT = "INSERT INTO "
					+ Tables.SAVINGS
					+ " (TRANS_TYPE, TRANS_DATE, AMOUNT, DESCRIPTION, SUM_AMOUNT) "
					+ "Values(\"Income\", \"" + tran.getDate() + "\", \""
					+ tran.getAmount() + "\", \"" + tran.getDescription()
					+ "\", \"" + tran.getAmount() + "\");";
			try {
				ps = con.prepareStatement(SQL_TEXT);
				logger.debug("Savings Query...\n" + SQL_TEXT);
				recordsInserted3 = ps.executeUpdate();
			} catch (SQLException e) {
				throw new AppException(e);
			}
		}
		cleanup();
		int totalRecordsInserted = recordsInserted1 + recordsInserted2
				+ recordsInserted3;
		logger.info("Success, inserted " + totalRecordsInserted + " records");
	}

	private static void cleanup() {
		try {
			ps.close();
		} catch (SQLException e) {
			throw new AppException(e);
		}
	}
}

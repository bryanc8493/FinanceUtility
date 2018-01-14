package com.bryan.finance.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.bryan.finance.beans.ReportRecord;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;

public class DeleteTransaction {

	private static Logger logger = Logger.getLogger(DeleteTransaction.class);
	private static final String MonthlyTransSQL = "Delete from "
			+ Tables.MONTHLY_TRANSACTIONS + " where TRANSACTION_ID = ?";
	private static String otherSQL;

	public static boolean removeAllTransactions(ReportRecord record, String id,
			final Connection con) {

		int ID = Integer.parseInt(id);
		PreparedStatement ps = null;

		Tables table = record.getType().equalsIgnoreCase(
				ApplicationLiterals.EXPENSE) ? Tables.EXPENSES : Tables.INCOME;

		logger.debug("Deleting monthly table tran ID = " + id);
		logger.debug("Deleting related " + table + " record: '"
				+ record.getTitle() + "' - '" + record.getDate() + "' - '"
				+ record.getAmount() + "'");
		otherSQL = "Delete from " + table + " where TITLE = '"
				+ record.getTitle() + "'" + " and TRANSACTION_DATE = '"
				+ record.getDate() + "'" + " and AMOUNT like '"
				+ record.getAmount() + "%'";

		try {
			ps = con.prepareStatement(MonthlyTransSQL);
			ps.setInt(1, ID);
			ps.execute();
			ps = con.prepareStatement(otherSQL);
			ps.execute();
			return true;
		} catch (SQLException e) {
			throw new AppException(e);
		}
	}
}

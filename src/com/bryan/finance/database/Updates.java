package com.bryan.finance.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.swing.JOptionPane;

import com.bryan.finance.enums.Databases;
import org.apache.log4j.Logger;

import com.bryan.finance.beans.Transaction;
import com.bryan.finance.beans.UpdatedRecord;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.security.Encoding;

public class Updates {

	private static Logger logger = Logger.getLogger(Updates.class);

	public static void changeAddresses(List<UpdatedRecord> updates) {
		logger.debug("Updating address changes..");
		final Connection con = Connect.getConnection();
		for (UpdatedRecord a : updates) {
			String query = "UPDATE " + Databases.ACCOUNTS
					+ ApplicationLiterals.DOT + Tables.ADDRESSES
					+ " set attr = ? where ID = ?";
			query = query.replace("attr", a.getAttribute());
			try {
				PreparedStatement preparedStmt = con.prepareStatement(query);
				preparedStmt.setString(1, a.getData());
				preparedStmt.setString(2, a.getID());
				preparedStmt.executeUpdate();
			} catch (SQLException e) {
				throw new AppException(e);
			}
		}
	}

	public static void changeAccounts(List<UpdatedRecord> updates) {
		logger.debug("Making account updates...");
		final Connection con = Connect.getConnection();
		for (UpdatedRecord a : updates) {
			try {
				String query = "UPDATE " + Databases.ACCOUNTS + ApplicationLiterals.DOT + Tables.SITES
						+ " set attr = {data} where ID = {id}";
				query = query.replace("attr", a.getAttribute());
				if (a.getAttribute().equalsIgnoreCase("PASS")) {
					query = query.replace(
							"{data}",
							"AES_ENCRYPT('"
									+ a.getData()
									+ "', '"
									+ Encoding.decrypt(ApplicationLiterals
											.getEncryptionKey()) + "')");
				} else {
					query = query.replace("{data}", "'" + a.getData() + "'");
				}
				query = query.replace("{id}", a.getID());

				Statement statement = con.createStatement();
				statement.executeUpdate(query);
			} catch (Exception e) {
				throw new AppException(e);
			}
		}
		int updatedCount = updates.size();
		logger.debug("Updated " + updatedCount + " updates");
	}

	public static void deleteAddress(String ID) {
		logger.debug("Deleting Address...");
		final Connection con = Connect.getConnection();
		String query = "DELETE from " + Databases.ACCOUNTS + ApplicationLiterals.DOT +
				Tables.ADDRESSES + " WHERE ID = " + ID;
		try {
			Statement statement = con.createStatement();
			statement.executeUpdate(query);
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

	public static void deleteAccount(String ID) {
		logger.debug("Deleting account...");
		final Connection con = Connect.getConnection();
		String query = "DELETE from " + Databases.ACCOUNTS + ApplicationLiterals.DOT
				+ Tables.SITES + " WHERE ID = " + ID;
		try {
			Statement statement = con.createStatement();
			statement.executeUpdate(query);
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

	public static void changeTransactions(List<UpdatedRecord> updates) {
		logger.debug("Updating address changes..");
		final Connection con = Connect.getConnection();
		for (UpdatedRecord a : updates) {
			String query = "UPDATE " + Databases.FINANCIAL
					+ ApplicationLiterals.DOT + Tables.MONTHLY_TRANSACTIONS
					+ " set attr = ? where TRANSACTION_ID = ?";
			query = query.replace("attr", a.getAttribute());
			try {
				PreparedStatement preparedStmt = con.prepareStatement(query);
				preparedStmt.setString(1, a.getData());
				preparedStmt.setString(2, a.getID());
				preparedStmt.executeUpdate();
			} catch (SQLException e) {
				throw new AppException(e);
			}
		}
		int updatedCount = updates.size();
		logger.debug("Made " + updatedCount + " updates");
		JOptionPane.showMessageDialog(null, "Successfully updated "
						+ updatedCount + " transaction records", "Updated!",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void deleteTransaction(Transaction tran) {
		logger.debug("Deleting Transactions...");
		final Connection con = Connect.getConnection();
		int monthlyTableDel = 0;
		int expenseTableDel = 0;
		int incomeTableDel = 0;

		String query = "DELETE from " + Tables.MONTHLY_TRANSACTIONS
				+ " WHERE TRANSACTION_ID = " + tran.getTransactionID();
		try {
			Statement statement = con.createStatement();
			monthlyTableDel = statement.executeUpdate(query);
		} catch (Exception e) {
			throw new AppException(e);
		}

		if (tran.getType().equals(ApplicationLiterals.EXPENSE)) {
			String expenseQuery = "DELETE from " + Tables.EXPENSES
					+ " WHERE TITLE = '" + tran.getTitle() + "' "
					+ "AND TRANSACTION_DATE = '" + tran.getDate() + "' "
					+ "AND AMOUNT = '" + tran.getAmount() + "'";
			try {
				Statement statement = con.createStatement();
				expenseTableDel = statement.executeUpdate(expenseQuery);
			} catch (Exception e) {
				throw new AppException(e);
			}

		} else {
			String incomeQuery = "DELETE from " + Tables.INCOME
					+ " WHERE TITLE = '" + tran.getTitle() + "' "
					+ "AND TRANSACTION_DATE = '" + tran.getDate() + "' "
					+ "AND AMOUNT = '" + tran.getAmount() + "'";
			try {
				Statement statement = con.createStatement();
				incomeTableDel = statement.executeUpdate(incomeQuery);
			} catch (Exception e) {
				throw new AppException(e);
			}
		}

		int totalDeleted = incomeTableDel + expenseTableDel + monthlyTableDel;
		logger.debug("Deleted " + totalDeleted + " trans");
		JOptionPane.showMessageDialog(null, "Deleted " + totalDeleted
				+ " total records successfully", "Deleted!",
				JOptionPane.INFORMATION_MESSAGE);
	}
}

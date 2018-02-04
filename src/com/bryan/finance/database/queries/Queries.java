package com.bryan.finance.database.queries;

import java.awt.Dimension;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.bryan.finance.database.Connect;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import org.apache.log4j.Logger;

import com.bryan.finance.beans.Account;
import com.bryan.finance.beans.Address;
import com.bryan.finance.beans.Salary;
import com.bryan.finance.beans.Transaction;
import com.bryan.finance.beans.User;
import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.VerifyAccess;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.security.Encoding;

public class Queries {

	private static Logger logger = Logger.getLogger(Queries.class);

	public static Object[][] getAddresses() {
		logger.debug("Getting addresses...");
		final Connection con = Connect.getConnection();
		String SQL_TEXT = "select count(*) from accounts.addresses";
		Statement statement;
		ResultSet rs;
		int recordCount = 0;
		try {
			statement = con.createStatement();
			rs = statement.executeQuery(SQL_TEXT);
			while (rs.next()) {
				recordCount = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new AppException(e);
		}

		Object[][] records = new Object[recordCount][7];

		SQL_TEXT = "SELECT * FROM accounts.addresses order by LAST_NAME ASC";
		try {
			rs = statement.executeQuery(SQL_TEXT);
			recordCount = 0;
			while (rs.next()) {
				records[recordCount][0] = rs.getString(1);
				records[recordCount][1] = rs.getString(2);
				records[recordCount][2] = rs.getString(3);
				records[recordCount][3] = rs.getString(4);
				records[recordCount][4] = rs.getString(5);
				records[recordCount][5] = rs.getString(6);
				records[recordCount][6] = rs.getString(7);
				recordCount++;
			}
		} catch (SQLException e1) {
			throw new AppException(e1);
		}

		return records;
	}

	public static int newAddress(Address address) {
		logger.debug("Adding new address");
		final Connection con = Connect.getConnection();
		String lastName = address.getLastName();
		String firstName = address.getFirstName();
		String addr = address.getAddress();
		String city = address.getCity();
		String state = address.getState();
		String zip = address.getZipcode();

		String SQL_TEXT = "INSERT INTO accounts.addresses (LAST_NAME, FIRST_NAMES, ADDRESS, CITY, STATE, ZIP) "
				+ "VALUES('"
				+ lastName
				+ "', '"
				+ firstName
				+ "', '"
				+ addr
				+ "', '" + city + "', '" + state + "', '" + zip + "')";
		PreparedStatement ps;

		int recordsInserted = 0;
		try {
			ps = con.prepareStatement(SQL_TEXT);
			recordsInserted = ps.executeUpdate();
		} catch (SQLException e) {
			throw new AppException(e);
		}

		return recordsInserted;
	}

	public static String getFuturePayments() {
		logger.debug("Determining Future Payments...");
		final Connection con = Connect.getConnection();
		String SQL_TEXT = "select SUM(COMBINED_AMOUNT) from financial.monthly_transactions WHERE TRANSACTION_DATE > now()";
		Statement statement;
		ResultSet rs;
		String amount = null;
		try {
			statement = con.createStatement();
			rs = statement.executeQuery(SQL_TEXT);
			while (rs.next()) {
				amount = rs.getString(1);
			}
		} catch (SQLException e) {
			throw new AppException(e);
		}

		return amount;
	}

	public static int newUser(User user) throws Exception {
		logger.debug("Creating new user: " + user.getUsername());
		final Connection con = Connect.getConnection();
		String SQL_TEXT = "INSERT INTO accounts.users " + "VALUES('"
				+ user.getUsername() + "', '" + user.getEmail() + "', "
				+ "AES_ENCRYPT('" + user.getPassword() + "', '"
				+ Encoding.decrypt(ApplicationLiterals.getEncryptionKey())
				+ "'), now(), " + "'" + user.getPermission() + "', '"
				+ user.getStatus() + "')";

		PreparedStatement ps;

		int recordsInserted;
		try {
			ps = con.prepareStatement(SQL_TEXT);
			recordsInserted = ps.executeUpdate();
		} catch (SQLException e) {
			throw new AppException(e);
		}
		return recordsInserted;
	}

	public static Set<User> getAllUsers() {

		Set<User> users = new LinkedHashSet<>();
		logger.debug("Getting all users...");
		String SQL_TEXT = "SELECT * FROM accounts.users where USERNAME <> 'ROOT'";
		Statement statement;
		ResultSet rs;

		try {
			Connection con = Connect.getConnection();
			statement = con.createStatement();
			rs = statement.executeQuery(SQL_TEXT);
			while (rs.next()) {
				User u = new User();
				u.setUsername(rs.getString(1));
				u.setEmail(rs.getString(2));
				u.setLastLogin(rs.getString(4));
				u.setPermission(rs.getString(5).charAt(0));
				u.setStatus(rs.getString(6));
				users.add(u);
			}
			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
		return users;
	}

	public static void lockUser(String user) {

		try {
			Connection con = Connect.getConnection();
			String[] systemInfo = VerifyAccess.getSystemInfo();

			PreparedStatement ps;
			String SQL_TEXT = ("INSERT INTO accounts.banned_users VALUES('"
					+ systemInfo[0] + "'," + "'" + systemInfo[1] + "', '"
					+ systemInfo[2] + "', now(), '" + user + "')");
			logger.warn("Locking user: " + user);

			ps = con.prepareStatement(SQL_TEXT);
			ps.executeUpdate();

			SQL_TEXT = "UPDATE accounts.users SET STATUS = 'LOCKED' where USERNAME = '"
					+ user + "'";
			ps = con.prepareStatement(SQL_TEXT);
			ps.executeUpdate();

			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

	public static void unlockUser(String user) {

		try {
			Connection con = Connect.getConnection();

			PreparedStatement ps;
			String SQL_TEXT = "UPDATE accounts.users SET STATUS = 'UNLOCKED' where USERNAME = '"
					+ user + "'";
			logger.warn("Unlocking user: " + user);

			ps = con.prepareStatement(SQL_TEXT);
			ps.executeUpdate();

			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

	public static Address getSpecifiedAddress(String lastName, String firstName) {
		logger.debug("Getting Address record for " + lastName + " - "
				+ firstName + "...");
		final Connection con = Connect.getConnection();
		String SQL_TEXT = "SELECT LAST_NAME, FIRST_NAMES, ADDRESS, CITY, STATE, ZIP "
				+ "FROM accounts.addresses where LAST_NAME = '"
				+ lastName
				+ "' " + "AND FIRST_NAMES = '" + firstName + "'";
		Statement statement;
		ResultSet rs;
		Address address = new Address();

		try {
			statement = con.createStatement();
			rs = statement.executeQuery(SQL_TEXT);
			while (rs.next()) {
				address.setLastName(rs.getString(1));
				address.setFirstName(rs.getString(2));
				address.setAddress(rs.getString(3));
				address.setCity(rs.getString(4));
				address.setState(rs.getString(5));
				address.setZipcode(rs.getString(6));
			}
			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
		return address;
	}

	public static Transaction getSpecifiedTransaction(String tranId) {
		String SQL_TEXT = "SELECT TRANSACTION_ID, TITLE, TYPE, CATEGORY, TRANSACTION_DATE, AMOUNT, DESCRIPTION, CREDIT, CREDIT_PAID "
				+ "FROM monthly_transactions WHERE TRANSACTION_ID = " + tranId;
		Statement statement;
		ResultSet rs;
		Transaction tran = new Transaction();
		try {
			Connection con = Connect.getConnection();
			statement = con.createStatement();
			rs = statement.executeQuery(SQL_TEXT);
			while (rs.next()) {
				tran.setTransactionID(rs.getString(1));
				tran.setTitle(rs.getString(2));
				tran.setType(rs.getString(3));
				tran.setCategory(rs.getString(4));
				tran.setDate(rs.getString(5));
				tran.setAmount(rs.getString(6));
				tran.setDescription(rs.getString(7));
				tran.setCredit(rs.getString(8).charAt(0));
				tran.setCreditPaid(rs.getString(9).charAt(0));
			}
			con.close();
		} catch (StringIndexOutOfBoundsException e) {
			tran.setCreditPaid(' ');
		} catch (SQLException sqlE) {
			throw new AppException(sqlE);
		}
		return tran;
	}

	public static Set<Salary> getSalaryData() {
		logger.debug("Getting all salary data");
		String SQL_TEXT = "SELECT * from financial.pay_grades";
		Statement statement = null;
		ResultSet rs = null;
		Set<Salary> data = new LinkedHashSet<Salary>();

		try {
			Connection con = Connect.getConnection();
			statement = con.createStatement();
			rs = statement.executeQuery(SQL_TEXT);
			while (rs.next()) {
				Salary salary = new Salary();
				salary.setGrade(rs.getInt(1));
				salary.setMinPay(rs.getInt(2));
				salary.setMidPay(rs.getInt(3));
				salary.setMaxPay(rs.getInt(4));
				salary.setStiTarget(rs.getInt(5));
				salary.setStiMax(rs.getInt(6));
				salary.setMtiTarget(rs.getInt(7));
				salary.setMtiMax(rs.getInt(8));
				data.add(salary);
			}
			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
		return data;
	}

	public static JScrollPane getFutureRecordsPane() {
		JScrollPane sp = null;
		String SQL_TEXT = "SELECT count(*) from financial.expenses where TRANSACTION_DATE > now()";
		Statement statement = null;
		ResultSet rs = null;

		try {
			Connection con = Connect.getConnection();
			statement = con.createStatement();
			rs = statement.executeQuery(SQL_TEXT);
			rs.next();
			int count = rs.getInt(1);
			SQL_TEXT = SQL_TEXT.replace("expenses", "income");
			rs = statement.executeQuery(SQL_TEXT);
			rs.next();
			count += rs.getInt(1);

			Object[][] data = new Object[count][5];
			Object[] columns = { "Title", "Type", "Category", "Date", "Amount" };
			SQL_TEXT = "SELECT TITLE, CATEGORY, TRANSACTION_DATE, AMOUNT "
					+ "from financial.expenses where TRANSACTION_DATE > now()";
			rs = statement.executeQuery(SQL_TEXT);
			count = 0;

			while (rs.next()) {
				data[count][0] = rs.getString(1);
				data[count][1] = "Expense";
				data[count][2] = rs.getString(2);
				data[count][3] = rs.getString(3);
				data[count][4] = "( -" + rs.getString(4) + " )";
				count++;
			}

			SQL_TEXT = SQL_TEXT.replace("expenses", "income");
			rs = statement.executeQuery(SQL_TEXT);

			while (rs.next()) {
				data[count][0] = rs.getString(1);
				data[count][1] = "Income";
				data[count][2] = rs.getString(2);
				data[count][3] = rs.getString(3);
				data[count][4] = rs.getString(4);
				count++;
			}

			JTable table = new JTable(data, columns);
			sp = new JScrollPane(table);
			sp.setViewportView(table);
			sp.setVisible(true);
			Dimension d = table.getPreferredSize();
			sp.setPreferredSize(new Dimension((d.width * 2) - 150, table
					.getRowHeight() * 10));

			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
		return sp;
	}

	public static JScrollPane getCreditRecordsPane() {

		JScrollPane sp = null;
		String SQL_TEXT = "SELECT count(*) from financial.monthly_transactions "
				+ "where CREDIT = '1' AND CREDIT_PAID = '0'";
		Statement statement = null;
		ResultSet rs = null;

		try {
			Connection con = Connect.getConnection();
			statement = con.createStatement();
			rs = statement.executeQuery(SQL_TEXT);
			rs.next();
			int count = rs.getInt(1);

			Object[][] data = new Object[count][4];
			Object[] columns = { "Title", "Category", "Date", "Amount" };
			SQL_TEXT = "SELECT TITLE, CATEGORY, TRANSACTION_DATE, AMOUNT "
					+ "from financial.monthly_transactions "
					+ "where CREDIT = '1' AND CREDIT_PAID = '0'";
			rs = statement.executeQuery(SQL_TEXT);
			count = 0;

			while (rs.next()) {
				data[count][0] = rs.getString(1);
				data[count][1] = rs.getString(2);
				data[count][2] = rs.getString(3);
				data[count][3] = rs.getString(4);
				count++;
			}

			JTable table = new JTable(data, columns);
			sp = new JScrollPane(table);
			sp.setViewportView(table);
			sp.setVisible(true);
			Dimension d = table.getPreferredSize();
			sp.setPreferredSize(new Dimension(d.width * 2,
					table.getRowHeight() * 10));

			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
		return sp;
	}

	public static void markCreditsPaid(Set<Transaction> records) {

		try {
			Connection con = Connect.getConnection();

			PreparedStatement ps;
			String SQL_TEXT = "UPDATE " + Databases.FINANCIAL + ApplicationLiterals.DOT
					+ Tables.MONTHLY_TRANSACTIONS + " SET CREDIT_PAID = '1' "
					+ "where TRANSACTION_ID = ?";

			for (Transaction t : records) {
				System.out.println(t.getTransactionID());
				ps = con.prepareStatement(SQL_TEXT);
				ps.setString(1, t.getTransactionID());
				ps.executeUpdate();
			}

			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

	public static int setNewPassword(String input) {

		logger.debug("Changing password for user: " + Connect.getCurrentUser());
		int recordsInserted = 0;

		try {
			String SQL_TEXT = "UPDATE accounts.users "
					+ "SET ENCRYPTED_PASS = AES_ENCRYPT('" + input + "', '"
					+ Encoding.decrypt(ApplicationLiterals.getEncryptionKey())
					+ "') " + "WHERE USERNAME = '" + Connect.getCurrentUser()
					+ "'";

			Connection con = Connect.getConnection();
			PreparedStatement ps = null;
			ps = con.prepareStatement(SQL_TEXT);
			recordsInserted = ps.executeUpdate();
			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
		return recordsInserted;
	}

	public static int setNewPassword(String user, String input) {

		logger.debug("Changing password for user: " + user);
		int x = 0;
		try {
			String SQL_TEXT = "UPDATE accounts.users "
					+ "SET ENCRYPTED_PASS = AES_ENCRYPT('" + input + "', '"
					+ Encoding.decrypt(ApplicationLiterals.getEncryptionKey())
					+ "') " + "WHERE USERNAME = '" + user + "'";

			Connection con = Connect.getConnection();
			PreparedStatement ps = null;
			ps = con.prepareStatement(SQL_TEXT);
			x = ps.executeUpdate();
			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
		return x;
	}

	public static int resetPassword(String user) {

		int recordsInserted = 0;
		char permission = '1';
		Connection con = null;
		try {
			con = Connect.getConnection();
			String SQL_TEXT = "SELECT PERMISSION from accounts.users WHERE USERNAME = '"
					+ user + "'";

			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(SQL_TEXT);
			rs.next();
			permission = rs.getString(1).charAt(0);
		} catch (Exception e) {
			logger.error("Failed getting user's permission" + e.toString()
					+ Arrays.toString(e.getStackTrace()));
		}

		if (permission == '1') {
			JOptionPane.showMessageDialog(null,
					"Password cannot be changed for this user", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				String SQL_TEXT = "UPDATE accounts.users "
						+ "SET ENCRYPTED_PASS = AES_ENCRYPT('daisymae', '"
						+ Encoding.decrypt(ApplicationLiterals
								.getEncryptionKey()) + "') "
						+ "WHERE USERNAME = '" + user + "'";

				PreparedStatement ps = null;
				ps = con.prepareStatement(SQL_TEXT);
				recordsInserted = ps.executeUpdate();
				logger.debug("reset password for user: " + user);
				con.close();
			} catch (Exception e) {
				throw new AppException(e);
			}
		}
		return recordsInserted;
	}
}

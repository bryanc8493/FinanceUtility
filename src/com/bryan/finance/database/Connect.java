package com.bryan.finance.database;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.bryan.finance.beans.User;
import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.MainMenu;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.security.Encoding;

public class Connect extends ApplicationLiterals {

	private static char PERMISSION = '0';
	private static User currentUser;
	private static Logger logger = Logger.getLogger(Connect.class);

	private static Connection con;

	public static void InitialConnect(String user)
			throws GeneralSecurityException, IOException {

		initializeUser(user);

		String url = ReadConfig.getConfigValue(DB_URL);
		String className = ReadConfig.getConfigValue(MY_SQL_CLASS);
		String username = ReadConfig.getConfigValue(DB_USER);
		String Dpass = Encoding.decrypt(ReadConfig.getConfigValue(DB_PASS));

		logger.info("Establishing initial database connection...");

		try {
			Class.forName(className);
			con = DriverManager.getConnection(url, username, Dpass);
		} catch (Exception e) {
			throw new AppException(e);
		}

		updateUsersLastLogin();

		setUsersPermission();

		launchMainMenu();
	}

	public static Connection getConnection() {
		String url = ReadConfig.getConfigValue(DB_URL);
		String className = ReadConfig.getConfigValue(MY_SQL_CLASS);
		String username = ReadConfig.getConfigValue(DB_USER);
		String pass;

		Connection con;
		try {
			pass = Encoding.decrypt(ReadConfig.getConfigValue(DB_PASS));
			Class.forName(className);
			con = DriverManager.getConnection(url, username, pass);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error connecting to Database:"
					+ NEW_LINE + e.toString(), "Database Error",
					JOptionPane.ERROR_MESSAGE);
			throw new AppException(e);
		}
		return con;
	}

	public static String getCurrentUser() {
		return currentUser.getUsername();
	}

	public static char getUsersPermission() {
		return PERMISSION;
	}

	private static void initializeUser(String user) {
		currentUser = new User();
		currentUser.setUsername(user);
	}

	private static void updateUsersLastLogin() {
		PreparedStatement ps = null;
		String SQL_TEXT = ("UPDATE " + Databases.ACCOUNTS + DOT + Tables.USERS
				+ " set LAST_LOGIN = now() WHERE USERNAME = '"
				+ currentUser.getUsername() + "'");
		try {
			ps = con.prepareStatement(SQL_TEXT);
			ps.executeUpdate();
		} catch (SQLException e) {
			logger.error("Failed updating last login table - " + e.toString()
					+ Arrays.toString(e.getStackTrace()));
		}
	}

	private static void setUsersPermission() {
		String SQL_TEXT = "SELECT PERMISSION from " + Databases.ACCOUNTS + DOT
				+ Tables.USERS + " WHERE USERNAME = '"
				+ currentUser.getUsername() + "'";
		try {
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(SQL_TEXT);
			rs.next();
			PERMISSION = rs.getString(1).charAt(0);
			currentUser.setPermission(PERMISSION);
			logger.info("User " + currentUser.getUsername()
					+ " set with permission " + PERMISSION);
		} catch (Exception e) {
			logger.error("Failed getting user's permission, default to 0 - "
					+ e.toString() + Arrays.toString(e.getStackTrace()));
		}
	}

	private static void launchMainMenu() {
		if (con != null) {
			logger.info("Connected successfully, logged in as user: "
					+ getCurrentUser());
			MainMenu.modeSelection(false, 0);
		}
	}
}

package com.bryan.finance.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.database.Connect;
import com.bryan.finance.database.Queries;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.account.NewUser;
import com.bryan.finance.gui.account.UserManagement;
import com.bryan.finance.gui.util.Loading;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.program.FinanceUtility;
import com.bryan.finance.security.Encoding;
import com.bryan.finance.utilities.HintPassField;
import com.bryan.finance.utilities.HintTextField;

public class VerifyAccess extends ApplicationLiterals {

	private static int attempts = 0;
	private static Logger logger = Logger.getLogger(VerifyAccess.class);

	private static final Font font = new Font("Sans serif", Font.PLAIN, 16);

	private static JFrame frame;

	public static void CheckAccess() {

		logger.debug("Displaying GUI Prompting verification");
		frame = new JFrame("Verification");
		JPanel p = new JPanel();
		JLabel title = new JLabel(ApplicationLiterals.APP_TITLE,
				SwingConstants.CENTER);
		title.setFont(new Font("sans serif", Font.BOLD, 22));
		title.setForeground(Color.darkGray);
		final HintTextField userField = new HintTextField("Username", true);
		final HintPassField passField = new HintPassField("Password", true);

		final JButton submit = new JButton(Icons.LOGIN_ICON);
		submit.setBorder(null);
		submit.setCursor(new Cursor(Cursor.HAND_CURSOR));
		final JButton create = new JButton("<html><u>Create Account</u></html>");
		makeButtonLink(create);
		final JButton forgot = new JButton(
				"<html><u>Forgot Password?</u></html>");
		makeButtonLink(forgot);

		JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,
				0));
		passwordPanel.add(passField);
		passField.setColumns(13);

		final JButton showButton = new JButton(Icons.SHOW_PASS_ICON);
		final JButton hideButton = new JButton(Icons.HIDE_PASS_ICON);
		hideButton.setBorder(null);
		hideButton.setVisible(false);
		showButton.setBorder(null);
		passwordPanel.add(showButton);
		passwordPanel.add(hideButton);

		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		bottom.add(create);
		bottom.add(forgot);

		JPanel input = new JPanel(new BorderLayout(0, 15));
		input.add(userField, BorderLayout.NORTH);
		input.add(passwordPanel, BorderLayout.CENTER);
		input.add(bottom, BorderLayout.SOUTH);

		p.setLayout(new BorderLayout(0, 15));
		p.add(title, BorderLayout.NORTH);
		p.add(input, BorderLayout.CENTER);
		p.add(submit, BorderLayout.SOUTH);

		p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		title.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

		frame.add(p);
		frame.setIconImage(Icons.APP_ICON.getImage());
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);
		frame.pack();
		Loading.terminate();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		JRootPane rp = SwingUtilities.getRootPane(submit);
		rp.setDefaultButton(submit);

		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = userField.getText().trim().toUpperCase();
				if (username.equals(ApplicationLiterals.EMPTY)) {
					logger.debug("User name field was empty");
					JOptionPane.showMessageDialog(frame,
							"The username field cannot be empty!",
							"No Username", JOptionPane.WARNING_MESSAGE);
					userField.requestFocusInWindow();
					return;
				}

				if (!doesUsernameExist(username)) {
					logger.warn("Username " + username + " does not exist."
							+ ApplicationLiterals.NEW_LINE
							+ "Try again or create new account");
					JOptionPane.showMessageDialog(frame, "Username " + username
							+ " does not exist." + ApplicationLiterals.NEW_LINE
							+ "Try again or create new account",
							"Invalid User", JOptionPane.ERROR_MESSAGE);
					passField.setText("");
					frame.pack();
				} else {
					verifyNotBanned(username);

					if (validPassword(username,
							new String(passField.getPassword()))) {
						frame.dispose();
						logger.debug("Login successful"
								+ ApplicationLiterals.NEW_LINE
								+ "Launching Application from GUI");
						// if password is default it MUST be reset before
						// entering app
						String defaultPassword = ReadConfig
								.getConfigValue(ApplicationLiterals.DEFAULT_PASSWORD);
						if (new String(passField.getPassword())
								.equals(defaultPassword)) {
							UserManagement.changePassword(true, username);
						} else {
							try {
								Connect.InitialConnect(username);
							} catch (GeneralSecurityException | IOException e1) {
								throw new AppException(e1);
							}
						}
					} else {
						attempts++;
						if (attempts == 3) {
							frame.dispose();
							banUser(username);
						}
						logger.debug("Incorrect password");
						passField.setText(ApplicationLiterals.EMPTY);
						int result = JOptionPane.showOptionDialog(null,
								"Incorrect Password!", "Incorrect",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.ERROR_MESSAGE, null, new String[] {
										"Ok", "Forgot Password?" },
								JOptionPane.NO_OPTION);
						if (result == JOptionPane.NO_OPTION) {
							resetUserPassword();
						}
						frame.pack();
					}
				}
			}
		});

		showButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showButton.setVisible(false);
				hideButton.setVisible(true);
				passField.setEchoChar((char)0);
			}
		});

		hideButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideButton.setVisible(false);
				showButton.setVisible(true);
				passField.setEchoChar('•');
			}
		});

		forgot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				forgot.setForeground(LINK_CLICKED);
				resetUserPassword();
			}
		});

		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				create.setForeground(LINK_CLICKED);
				NewUser.createUser();
			}
		});
	}

	private static void resetUserPassword() {
		String user = JOptionPane.showInputDialog(frame,
				"Please input your username", "Reset",
				JOptionPane.INFORMATION_MESSAGE);
		String admin = ReadConfig
				.getConfigValue(ApplicationLiterals.ADMINISTRATOR);
		if (user.equalsIgnoreCase("root") || user.equalsIgnoreCase(admin)) {
			JOptionPane.showMessageDialog(frame,
					"Cannot use forgot password for this type of user!",
					"Unauthorized", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (Queries.resetPassword(user.trim()) > 0) {
			String defaultPassword = ReadConfig
					.getConfigValue(ApplicationLiterals.DEFAULT_PASSWORD);
			JOptionPane
					.showMessageDialog(
							frame,
							"<html>Password was successfully reset to the default: "
									+ "<i>"
									+ defaultPassword
									+ "</i><br><br>Copy this, and login soon, as you have 5 minutes to login "
									+ "and set a new passoword.</html>",
							"Password Reset", JOptionPane.WARNING_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(frame, "Unknown Error!  Check logs",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void banUser(String user) {
		Queries.lockUser(user);

		FinanceUtility.appLogger.logFooter();

		JOptionPane.showMessageDialog(null,
				"Due to too many password attempts, you have"
						+ "\nbeen locked out of the application", "Banned",
				JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}

	private static void verifyNotBanned(String user) {
		String status = "";
		try {
			Connection con = Connect.getConnection();

			String SQL_TEXT = ("SELECT STATUS FROM " + Databases.ACCOUNTS
					+ ApplicationLiterals.DOT + Tables.USERS
					+ " where USERNAME = '" + user + "'");

			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(SQL_TEXT);

			rs.next();
			try {
				status = rs.getString(1);
			} catch (SQLException s) {

			}

			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}

		if (status.equalsIgnoreCase(ApplicationLiterals.LOCKED)) {
			logger.warn(user + " user account is locked out");
			FinanceUtility.appLogger.logFooter();
			JOptionPane.showMessageDialog(null,
					"Your user account has been locked out of the application",
					"Banned", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	public static String[] getSystemInfo() {
		InetAddress ip;
		String hostname;
		String username;
		String[] data = new String[3];
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			username = System.getProperty(ApplicationLiterals.USER_NAME);
			data[0] = ip.toString();
			data[1] = hostname;
			data[2] = username;
		} catch (UnknownHostException e) {
			logger.warn("unable to log system info");
		}
		return data;
	}

	public static boolean doesUsernameExist(String user) {

		user = user.toUpperCase();
		boolean exists = false;
		try {
			Connection con = Connect.getConnection();

			String SQL_TEXT = ("SELECT USERNAME from " + Databases.ACCOUNTS
					+ ApplicationLiterals.DOT + Tables.USERS
					+ " WHERE USERNAME = '" + user + "'");

			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(SQL_TEXT);

			rs.next();
			try {
				rs.getString(1);
				exists = true;
			} catch (Exception e) {
				exists = false;
			}

			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
		return exists;
	}

	private static boolean validPassword(String user, String pass) {

		boolean valid = false;
		try {
			Connection con = Connect.getConnection();

			String SQL_TEXT = ("select AES_DECRYPT(ENCRYPTED_PASS, '"
					+ Encoding.decrypt(ApplicationLiterals.getEncryptionKey())
					+ "') from " + Databases.ACCOUNTS + ApplicationLiterals.DOT
					+ Tables.USERS + " where USERNAME ='" + user + "'");

			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(SQL_TEXT);

			rs.next();

			String result = rs.getString(1);
			if (pass.equals(result)) {
				valid = true;
			}

			con.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
		return valid;
	}

	private static void makeButtonLink(JButton b) {

		b.setBorder(null);
		b.setForeground(LINK_NOT_CLICKED);
		b.setCursor(new Cursor(Cursor.HAND_CURSOR));
		b.setOpaque(false);
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
	}
}
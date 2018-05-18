package com.bryan.finance.gui.account;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.bryan.finance.database.queries.Accounts;
import org.apache.log4j.Logger;

import com.bryan.finance.beans.User;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.VerifyAccess;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;

public class NewUser {

	private static Logger logger = Logger.getLogger(NewUser.class);

	public static void createUser() {
		logger.debug("Displaying GUI to create new user");
		final JFrame frame = new JFrame("New User");

		final JLabel usernameLabel = new JLabel("* Username");
		final JTextField usernameField = new JTextField();

		final JLabel emailLabel = new JLabel("* Email Address");
		final JTextField emailField = new JTextField();

		final JLabel passLabel = new JLabel("* Password");
		final JPasswordField passField = new JPasswordField();

		final JLabel confPassLabel = new JLabel(
				"* Confirm Password              ");
		final JPasswordField confPassField = new JPasswordField();

		final JButton insert = new PrimaryButton("    Insert    ");
		final JButton close = new PrimaryButton("    Close    ");

		final JLabel missingField = new JLabel();
		missingField.setForeground(Color.RED);
		missingField.setVisible(false);

		JPanel grid = new JPanel();
		grid.setLayout(new GridLayout(4, 2, 5, 10));
		grid.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
		grid.add(usernameLabel);
		grid.add(usernameField);
		grid.add(emailLabel);
		grid.add(emailField);
		grid.add(passLabel);
		grid.add(passField);
		grid.add(confPassLabel);
		grid.add(confPassField);

		JPanel missing = new JPanel();
		missing.setLayout(new FlowLayout(FlowLayout.CENTER));
		missing.add(missingField);

		JPanel middle = new JPanel();
		middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
		middle.add(grid);
		middle.add(missing);

		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttons.add(close);
		buttons.add(insert);

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		JLabel frameTitle = new Title("Create New User");
		main.add(frameTitle, BorderLayout.NORTH);
		main.add(middle, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.SOUTH);

		frame.add(main);
		frame.setIconImage(Icons.APP_ICON.getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		JRootPane rp = SwingUtilities.getRootPane(insert);
		rp.setDefaultButton(insert);
		rp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		close.addActionListener((e) -> frame.dispose());

		insert.addActionListener(e -> {
			// Verify email field is not blank
			if (emailField.getText().trim()
					.equals(ApplicationLiterals.EMPTY)) {
				missingField.setText("email address cannot be blank");
				missingField.setVisible(true);
				frame.pack();
			}

			// verify username field is not blank
			else if (usernameField.getText().trim()
					.equals(ApplicationLiterals.EMPTY)) {
				missingField.setText("Username cannot be blank");
				missingField.setVisible(true);
				frame.pack();
			}

			// verify password field is not blank
			else if (new String(passField.getPassword()).trim().equals(
					ApplicationLiterals.EMPTY)) {
				missingField.setText("Password cannot be blank");
				missingField.setVisible(true);
				frame.pack();
			}

			// verify password and confirm password contents match
			else if (!new String(passField.getPassword()).trim().equals(
					new String(confPassField.getPassword()).trim())) {
				missingField.setText("Passwords must match");
				missingField.setVisible(true);
				frame.pack();
			}

			// Verify username doesn't already exist
			else if (Accounts.doesUsernameExist(usernameField.getText()
					.trim().toUpperCase())) {
				missingField.setText("Username already exists!");
				missingField.setVisible(true);
				frame.pack();
			}

			// Call method to insert new account
			else {
				User user = new User();
				user.setUsername(usernameField.getText().trim()
						.toUpperCase());
				user.setEmail(emailField.getText().trim());
				user.setPassword(new String(passField.getPassword()));
				user.setStatus(ApplicationLiterals.UNLOCKED);
				user.setPermission(ApplicationLiterals.VIEW_ONLY);
				int recordCount;
				try {
					recordCount = Accounts.newUser(user);
				} catch (Exception e1) {
					throw new AppException(e1);
				}

				if (recordCount != 1) {
					missingField
							.setText("Error creating new user - check database");
					missingField.setVisible(true);
					frame.pack();
				} else {
					JOptionPane.showMessageDialog(null,
							"Accounts created successfully!", "Success",
							JOptionPane.INFORMATION_MESSAGE);
					frame.dispose();
				}
			}
		});
	}
}

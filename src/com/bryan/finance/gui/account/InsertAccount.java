package com.bryan.finance.gui.account;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import com.bryan.finance.utilities.HintPassField;
import com.bryan.finance.utilities.HintTextField;
import org.apache.log4j.Logger;

import com.bryan.finance.beans.Account;
import com.bryan.finance.database.queries.Queries;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.Icons;

public class InsertAccount {

	private static Logger logger = Logger.getLogger(InsertAccount.class);

	public static void addNewAccount() {
		logger.debug("Displaying frame for new account");
		final JFrame frame = new JFrame("New Accounts");
		JLabel frameTitle = new Title("Insert New Accounts");

		final JTextField accountField = new HintTextField("Accounts Name", false);
		accountField.setColumns(18);
		final JTextField usernameField = new HintTextField("Username", false);
		final JPasswordField passField = new HintPassField("Password", false);
		final JPasswordField confPassField = new HintPassField("Confirm Password", false);

		final JButton insert = new PrimaryButton("    Insert    ");
		final JButton close = new PrimaryButton("    Close    ");

		final JLabel missingField = new JLabel();
		missingField.setForeground(Color.RED);
		missingField.setVisible(false);

		JPanel grid = new JPanel();
		grid.setLayout(new GridLayout(4, 1, 5, 15));
		grid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		grid.add(accountField);
		grid.add(usernameField);
		grid.add(passField);
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

		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});

		insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Verify account name field is not blank
				if (accountField.getText().trim().equals("")) {
					missingField.setText("Accounts name cannot be blank");
					missingField.setVisible(true);
					frame.pack();
				}

				// verify username field is not blank
				else if (usernameField.getText().trim().equals("")) {
					missingField.setText("Username cannot be blank");
					missingField.setVisible(true);
					frame.pack();
				}

				// verify password field is not blank
				else if (new String(passField.getPassword()).trim().equals("")) {
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

				// Call method to insert new account
				else {
					Account account = new Account();
					account.setAccount(accountField.getText().trim());
					account.setUsername(usernameField.getText().trim());
					account.setPassword(new String(passField.getPassword())
							.trim());
					int recordCount;
					try {
						recordCount = Accounts.newAccount(account);
					} catch (Exception e1) {
						throw new AppException(e1);
					}

					if (recordCount != 1) {
						missingField
								.setText("Error inserting new account - check database");
						logger.error("Error inserting new account - check database");
						missingField.setVisible(true);
						frame.pack();
					} else {
						frame.dispose();
						JOptionPane.showMessageDialog(null,
								"New Accounts added successfully!", "Success",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
	}
}

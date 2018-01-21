package com.bryan.finance.gui.account;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.bryan.finance.beans.User;
import com.bryan.finance.database.Connect;
import com.bryan.finance.database.Queries;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.util.ButtonEditor;
import com.bryan.finance.gui.util.ButtonRenderer;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.utilities.HintPassField;

public class UserManagement extends JFrame {

	private static final long serialVersionUID = -2866411912769934047L;

	public UserManagement() {
		setTitle("Manage Users");

		JLabel title = new Title("Manage Users");
		JPanel p = new JPanel(new BorderLayout());
		JButton close = new PrimaryButton("Close");

		JPanel button = new JPanel(new FlowLayout(FlowLayout.CENTER));
		button.add(close);

		p.add(title, BorderLayout.NORTH);
		p.add(createDataTable(), BorderLayout.CENTER);
		p.add(button, BorderLayout.SOUTH);

		add(p);
		setIconImage(Icons.APP_ICON.getImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JRootPane rp = SwingUtilities.getRootPane(close);
		rp.setDefaultButton(close);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);

		close.addActionListener(e -> {
			dispose();
		});
	}

	private JScrollPane createDataTable() {
		Set<User> users = Queries.getAllUsers();

		Object[][] records = convertSetTo2DArray(users);
		Object[] columnNames = { "Username", "Email", "Last Login",
				"Permission", "Status", "Lock/Unlock" };

		DefaultTableModel model = new DefaultTableModel(records, columnNames);

		JTable table = new JTable(model);
		table.setRowHeight(25);
		table.getColumn("Lock/Unlock").setCellRenderer(new ButtonRenderer());
		table.getColumn("Lock/Unlock").setCellEditor(
				new ButtonEditor(new JCheckBox(), this));
		final JScrollPane entriesScrollPane = new JScrollPane(table);
		entriesScrollPane.setViewportView(table);
		entriesScrollPane.setVisible(true);
		Dimension d = table.getPreferredSize();
		entriesScrollPane.setPreferredSize(new Dimension(d.width * 2, table
				.getRowHeight() * 8));

		return entriesScrollPane;
	}

	private Object[][] convertSetTo2DArray(Set<User> users) {
		Object[][] array = new Object[users.size()][6];
		int counter = 0;

		for (User u : users) {
			array[counter][0] = u.getUsername();
			array[counter][1] = u.getEmail();
			array[counter][2] = u.getLastLogin();
			array[counter][3] = u.getPermission();
			array[counter][4] = u.getStatus();
			array[counter][5] = u.getUsername()
					+ ApplicationLiterals.SEMI_COLON + u.getStatus();
			counter++;
		}
		return array;
	}

	public static void changePassword(final boolean mustReset, final String user) {

		final JFrame f = new JFrame("Change Password");
		JLabel title = new Title("Change Password");
		final JPasswordField newPass = new HintPassField("New Password", true);
		newPass.setColumns(18);
		final JPasswordField newPassConf = new HintPassField("Confirm Password", true);

		JButton cancel = new PrimaryButton("Cancel");
		JButton submit = new PrimaryButton("Submit");

		JPanel grid = new JPanel(new GridLayout(2, 1, 10, 10));
		grid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		grid.add(newPass);
		grid.add(newPassConf);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(cancel);
		buttons.add(submit);

		JPanel p = new JPanel(new BorderLayout());
		p.add(title, BorderLayout.NORTH);
		p.add(grid, BorderLayout.CENTER);
		p.add(buttons, BorderLayout.SOUTH);

		f.add(p);
		f.setIconImage(Icons.APP_ICON.getImage());
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JRootPane rp = SwingUtilities.getRootPane(submit);
		rp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		rp.setDefaultButton(submit);
		f.pack();
		f.setVisible(true);
		f.setLocationRelativeTo(null);

		cancel.addActionListener(e -> {
			f.dispose();
		});

		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Make sure fields match
				String inputPass = new String(newPass.getPassword()).trim();
				String inputPassConf = new String(newPassConf.getPassword())
						.trim();
				if (inputPass.equals(inputPassConf)) {
					if (inputPass.length() > 3 && !mustReset) {
						if (Queries.setNewPassword(inputPass) > 0) {
							f.dispose();
							JOptionPane.showMessageDialog(null,
									"Password Updated Successfully!",
									"Success", JOptionPane.INFORMATION_MESSAGE);
						} else {
							JOptionPane
									.showMessageDialog(
											f,
											"Unknown error during password update.  Check logs.",
											"Failed", JOptionPane.ERROR_MESSAGE);
						}
					} else if (inputPass.length() > 3 && mustReset) {
						if (Queries.setNewPassword(user, inputPass) > 0) {
							f.dispose();
							JOptionPane.showMessageDialog(null,
									"Password Updated Successfully!",
									"Success", JOptionPane.INFORMATION_MESSAGE);
							try {
								Connect.InitialConnect(user);
							} catch (GeneralSecurityException | IOException e1) {
								throw new AppException(e1);
							}

						} else {
							JOptionPane
									.showMessageDialog(
											f,
											"Unknown error during password update.  Check logs.",
											"Failed", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(f,
								"Password must be at least 4 characters long",
								"Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(f, "Passwords do not match!",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
}

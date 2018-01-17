package com.bryan.finance.gui.account;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.bryan.finance.database.Connect;
import org.apache.log4j.Logger;

import com.bryan.finance.beans.UpdatedRecord;
import com.bryan.finance.database.Queries;
import com.bryan.finance.database.Updates;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.util.ApplicationControl;
import com.bryan.finance.gui.util.RequestFocusListener;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.security.Encoding;
import com.bryan.finance.utilities.HintPassField;
import com.bryan.finance.utilities.MultiLabelButton;

public class AccountsTab extends JPanel {

	private static final long serialVersionUID = -6032099937849808280L;

	private static Logger logger = Logger.getLogger(AccountsTab.class);
	private static JScrollPane acctSP;
	private static JTable table;
	private static JTable fullTable;
	private static TableColumn passwordColumn;
	private static List<UpdatedRecord> updates;

	private boolean passVerified = false;
	private int attempts = 1;

	public AccountsTab() {
		Connection con = Connect.getConnection();
		logger.debug("Initializing and populating Accounts Tab");
		getAccountData(false);

		final JButton view = new MultiLabelButton("View Passwords",
				MultiLabelButton.BOTTOM, Icons.VIEW_ICON);
		final JButton add = new MultiLabelButton(" New Account ",
				MultiLabelButton.BOTTOM, Icons.ADD_ICON);
		final JButton edit = new MultiLabelButton(" Edit Account ",
				MultiLabelButton.BOTTOM, Icons.EDIT_ICON);

		JLabel title = new Title("Current Accounts");

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(view);
		buttons.add(add);
		buttons.add(edit);
		buttons.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		JPanel content = new JPanel(new BorderLayout(0, 10));
		content.add(buttons, BorderLayout.NORTH);
		content.add(acctSP, BorderLayout.SOUTH);

		content.setBorder(ApplicationLiterals.PADDED_SPACE);

		this.setLayout(new BorderLayout(10, 10));
		this.add(title, BorderLayout.NORTH);
		this.add(content, BorderLayout.CENTER);
		this.add(
				ApplicationControl.closeAndLogout(con,
						(JFrame) SwingUtilities.getRoot(this)),
				BorderLayout.SOUTH);

		view.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkEncryptionKey()) {
					logger.debug("Displaying passwords");
					table.getColumnModel().addColumn(passwordColumn);
					Dimension d = table.getPreferredSize();
					acctSP.setPreferredSize(new Dimension(d.width * 3, table
							.getRowHeight() * 12));
					view.setEnabled(false);

				} else {
					logger.warn("Invalid encryption key - attempt: "
							+ getAttempts());
					setAttempts(getAttempts() + 1);
					if (getAttempts() > 3) {
						view.setEnabled(false);
						edit.setEnabled(false);
					}
				}
			}
		});

		add.addActionListener(e -> {
			InsertAccount.InsertFrame();

		});

		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!checkEncryptionKey()) {
					logger.warn("Invalid encryption key - attempt: "
							+ getAttempts());
					setAttempts(getAttempts() + 1);
					if (getAttempts() > 3) {
						edit.setEnabled(false);
						view.setEnabled(false);
					}
				} else {
					logger.debug("User editing account data");
					final JFrame f = new JFrame("Edit Accounts");
					JPanel p = new JPanel(new BorderLayout(10, 0));
					JLabel label = new Title("Edit Accounts");
					JButton update = new JButton("Update");
					JButton delete = new JButton("Delete");
					delete.setCursor(new Cursor(Cursor.HAND_CURSOR));
					update.setCursor(new Cursor(Cursor.HAND_CURSOR));
					JPanel buttons = new JPanel(new FlowLayout(
							FlowLayout.CENTER));
					buttons.add(delete);
					buttons.add(update);
					buttons.setBorder(BorderFactory.createEmptyBorder(10, 0,
							10, 0));
					p.add(label, BorderLayout.NORTH);
					p.add(getFullAccountData(), BorderLayout.CENTER);
					p.add(buttons, BorderLayout.SOUTH);
					f.add(p);
					f.pack();
					f.setVisible(true);
					f.setLocationRelativeTo(null);

					updates = new ArrayList<>();

					update.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							f.dispose();
							Updates.changeAccounts(updates);
						}
					});

					delete.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							int row = fullTable.getSelectedRow();
							if (row != -1) {
								String ID = (String) fullTable.getValueAt(row,
										0);
								int choice = JOptionPane
										.showConfirmDialog(
												null,
												"Are you sure you want to delete the selected record?",
												"Confirm",
												JOptionPane.YES_NO_OPTION);
								if (choice == JOptionPane.YES_OPTION) {
									f.dispose();
									Updates.deleteAccount(ID);
								}
							} else {
								JOptionPane.showMessageDialog(null,
										"Please select a record to delete",
										"No Selection",
										JOptionPane.WARNING_MESSAGE);
							}
						}
					});
				}
			}
		});
	}

	private void getAccountData(boolean showPassword) {
		Object[][] records = Queries.getAccounts();
		Object[] columnNames = { "ACCOUNT", "USERNAME", "PASSWORD" };
		table = new JTable(records, columnNames);
		passwordColumn = table.getColumnModel().getColumn(2);
		if (!showPassword) {
			table.getColumnModel().removeColumn(passwordColumn);
		}

		acctSP = new JScrollPane(table);
		acctSP.setViewportView(table);
		acctSP.setVisible(true);
		Dimension d = table.getPreferredSize();
		acctSP.setPreferredSize(new Dimension(d.width * 3,
				table.getRowHeight() * 12));
	}

	private JScrollPane getFullAccountData() {
		Object[][] records = Queries.getFullAccounts();
		Object[] columnNames = { "ID", "Account", "Username", "Password" };

		DefaultTableModel model = new DefaultTableModel(records, columnNames) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 0) {
					return false;
				} else {
					return true;
				}
			}
		};
		fullTable = new JTable(model);

		final JScrollPane sp = new JScrollPane(fullTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setViewportView(fullTable);
		sp.setVisible(true);
		Dimension d = fullTable.getPreferredSize();
		sp.setPreferredSize(new Dimension(d.width * 2,
				fullTable.getRowHeight() * 15));
		final Map<Integer, String> map = getAttributeMap();

		fullTable.getModel().addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				String changedData;
				String ID;
				int row = fullTable.getSelectedRow();
				int column = fullTable.getSelectedColumn();
				changedData = (String) fullTable.getValueAt(row, column);
				ID = (String) fullTable.getValueAt(row, 0);

				UpdatedRecord changedRecord = new UpdatedRecord();
				changedRecord.setID(ID);
				changedRecord.setAttribute(map.get(column));
				changedRecord.setData(changedData);
				updates.add(changedRecord);
			}
		});
		return sp;
	}

	private boolean checkEncryptionKey() {
		if (isPassVerified()) {
			return true;
		}
		JPasswordField pf = new HintPassField("Verify Encryption Key");
		pf.addAncestorListener(new RequestFocusListener());
		int okCxl = JOptionPane.showConfirmDialog(null, pf,
				"Verify Encryption Key", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (okCxl == JOptionPane.OK_OPTION) {
			String inputKey;
			try {
				inputKey = Encoding.encrypt(new String(pf.getPassword()));
			} catch (UnsupportedEncodingException | GeneralSecurityException e1) {
				throw new AppException(e1);
			}
			if (inputKey.equals(ApplicationLiterals.getEncryptionKey())) {
				logger.debug("Encryption Key is correct");
				setPassVerified(true);
				return true;
			}
		}
		JOptionPane.showMessageDialog(null, "Invalid key provided", "Invalid",
				JOptionPane.ERROR_MESSAGE);
		return false;
	}

	private static Map<Integer, String> getAttributeMap() {
		Map<Integer, String> map = new HashMap<>();
		map.put(1, "ACCOUNT");
		map.put(2, "USERNAME");
		map.put(3, "PASS");
		return map;
	}

	public boolean isPassVerified() {
		return passVerified;
	}

	public void setPassVerified(boolean passVerified) {
		this.passVerified = passVerified;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}
}

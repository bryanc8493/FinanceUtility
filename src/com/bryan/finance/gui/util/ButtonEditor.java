package com.bryan.finance.gui.util;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.bryan.finance.database.Connect;
import com.bryan.finance.database.queries.Accounts;
import org.apache.log4j.Logger;

import com.bryan.finance.gui.account.UserManagement;
import com.bryan.finance.literals.ApplicationLiterals;

public class ButtonEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 2957785944519810122L;
	protected JButton button;
	private String label;
	private boolean isPushed;
	private Logger logger = Logger.getLogger(ButtonEditor.class);
	private UserManagement currentFrame;
	private static final String currentUser = Connect.getCurrentUser();

	public ButtonEditor(JCheckBox checkBox, UserManagement current) {
		super(checkBox);
		button = new JButton();
		button.setOpaque(true);
		currentFrame = current;
		button.addActionListener(e -> {
			fireEditingStopped();
		});
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
												 boolean isSelected, int row, int column) {
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
		}
		label = (value == null) ? ApplicationLiterals.EMPTY : value.toString();
		isPushed = true;
		return button;
	}

	public Object getCellEditorValue() {
		if (isPushed) {
			String[] temp = label.split(ApplicationLiterals.SEMI_COLON);
			String user = temp[0];
			if(isCurrentUser(user))
				return label;
			String status = temp[1];
			String oppositeStatus = getOppositeStatus(status);

			int choice = getConfirmationChoice(status, oppositeStatus, user);

			if (choice == JOptionPane.YES_OPTION) {
				currentFrame.dispose();
				if (oppositeStatus.equalsIgnoreCase(ApplicationLiterals.LOCK)) {
					lockUser(user);
				} else {
					unlockUser(user);
				}
				new UserManagement();
			}
		}
		isPushed = false;
		return label;
	}

	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}

	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}

	private boolean isCurrentUser(String cellValue) {
		String[] split = cellValue.split(ApplicationLiterals.SEMI_COLON);

		if (split[0].equalsIgnoreCase(currentUser))
			return true;
		return false;
	}

	private int getConfirmationChoice(String status, String oppositeStatus, String user) {
		return JOptionPane.showConfirmDialog(null, user
						+ " is currently " + status + "."
						+ ApplicationLiterals.NEW_LINE + "Would you like to "
						+ oppositeStatus + " " + user + "?", "Confirm",
				JOptionPane.YES_NO_OPTION);
	}

	private void lockUser(String user) {
		logger.info("LOCKING USER: " + user);
		Accounts.lockUser(user);
		JOptionPane.showMessageDialog(null, user
						+ " has been locked", "Locked",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void unlockUser(String user) {
		logger.info("UNLOCKING USER: " + user);
		Accounts.unlockUser(user);
		JOptionPane.showMessageDialog(null, user
						+ " has been unlocked", "Unlocked",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private String getOppositeStatus(String status) {
		if (status.equalsIgnoreCase(ApplicationLiterals.UNLOCKED)) {
			return ApplicationLiterals.LOCK;
		} else {
			return ApplicationLiterals.UNLOCK;
		}
	}
}
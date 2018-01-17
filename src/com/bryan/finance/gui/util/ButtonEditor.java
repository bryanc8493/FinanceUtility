package com.bryan.finance.gui.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import org.apache.log4j.Logger;

import com.bryan.finance.database.Queries;
import com.bryan.finance.gui.account.UserManagement;
import com.bryan.finance.literals.ApplicationLiterals;

public class ButtonEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 2957785944519810122L;
	protected JButton button;
	private String label;
	private boolean isPushed;
	private Logger logger = Logger.getLogger(ButtonEditor.class);
	private UserManagement currentFrame;

	public ButtonEditor(JCheckBox checkBox, UserManagement current) {
		super(checkBox);
		button = new JButton();
		button.setOpaque(true);
		currentFrame = current;
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
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
			String status = temp[1];
			String oppositeStatus = ApplicationLiterals.EMPTY;
			if (status.equalsIgnoreCase(ApplicationLiterals.UNLOCKED)) {
				oppositeStatus = ApplicationLiterals.LOCK;
			} else {
				oppositeStatus = ApplicationLiterals.UNLOCK;
			}

			int choice = JOptionPane.showConfirmDialog(null, user
					+ " is currently " + status + "."
					+ ApplicationLiterals.NEW_LINE + "Would you like to "
					+ oppositeStatus + " " + user + "?", "Confirm",
					JOptionPane.YES_NO_OPTION);
			if (choice == JOptionPane.YES_OPTION) {
				currentFrame.dispose();
				if (oppositeStatus.equalsIgnoreCase(ApplicationLiterals.LOCK)) {
					logger.info("LOCKING USER: " + user);
					Queries.lockUser(user);
					JOptionPane.showMessageDialog(null, user
							+ " has been locked", "Locked",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					logger.info("UNLOCKING USER: " + user);
					Queries.unlockUser(user);
					JOptionPane.showMessageDialog(null, user
							+ " has been unlocked", "Unlocked",
							JOptionPane.INFORMATION_MESSAGE);
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
}
package com.bryan.finance.exception;

import javax.swing.JOptionPane;

public class BackupException extends Exception {

	private static final long serialVersionUID = 5479569327134895983L;
	private static final int ERROR = 1;
	private static final int WARN = 2;
	private static final int INFO = 3;

	public BackupException(String msg, String title, int type) {
		super(msg);
		if(type == ERROR) {
			JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
		}
		else if(type == WARN) {
			JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
		}
		else if(type == INFO) {
			JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
		}
	}
}

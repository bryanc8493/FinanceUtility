package com.bryan.finance.exception;

import java.util.Arrays;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.program.FinanceUtility;

public class AppException extends RuntimeException {

	private static final long serialVersionUID = -5372718122579774824L;
	private static final Logger logger = Logger.getLogger(AppException.class);

	public AppException(Exception e) {
		super(e);
		String msgAndStacktrace = e.toString()
				+ ApplicationLiterals.NEW_LINE
				+ Arrays.toString(e.getStackTrace()).replace(
						ApplicationLiterals.COMMA, "\r\n");
		logger.fatal("Application Exception: " + msgAndStacktrace);
		FinanceUtility.appLogger.logFooter();
		JOptionPane.showMessageDialog(null, e.toString()
				+ "\n\nCheck logs for more info", "Application Exception",
				JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}
}

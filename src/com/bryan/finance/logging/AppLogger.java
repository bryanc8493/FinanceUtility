package com.bryan.finance.logging;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;

public class AppLogger {

	private Logger logger;

	public AppLogger() {
		logger = Logger.getLogger(this.getClass());
		String log4JPropertyFile = ApplicationLiterals.getLaunchPath().replace(
				ApplicationLiterals.BIN, ApplicationLiterals.EMPTY)
				+ ApplicationLiterals.LOG_PROPERTY_FILE;
		Properties p = new Properties();

		try {
			p.load(new FileInputStream(log4JPropertyFile));
			PropertyConfigurator.configure(p);
			logHeader();
		} catch (IOException e) {
			throw new AppException(e);
		}
	}

	private void logHeader() {

		logger.info("===========================================");
		logger.info("STARTING APPLICATION");
		logger.info("===========================================");
	}

	public void logFooter() {

		logger.info("===========================================");
		logger.info("APPLICATION CLOSED");
		logger.info("===========================================\n\n\n");
	}
}

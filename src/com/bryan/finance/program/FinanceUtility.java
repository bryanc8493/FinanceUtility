package com.bryan.finance.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.swing.UIManager;

import com.bryan.finance.gui.reminder.ModifyReminders;
import org.apache.log4j.Logger;

import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.database.Connect;
import com.bryan.finance.gui.VerifyAccess;
import com.bryan.finance.gui.util.Loading;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.logging.AppLogger;

/**
 * Application Entry Point
 */
public class FinanceUtility {

	private static boolean USER_VALIDATION = true;
	private static Logger logger;
	public static AppLogger appLogger;

	public static void main(String[] args) throws GeneralSecurityException,
			IOException {
		appLogger = new AppLogger();
		logger = Logger.getLogger(FinanceUtility.class);

		// Check args if launched by user or task scheduler
		if (args.length > 0) {
			if (isValidArgs(args)) {
				new ModifyReminders(true);
			}
		} else{
			runApp();
		}

	}

	private static boolean isValidArgs(String[] args) {
		if (args.length != 1) {
			logger.fatal("Invalid args passed: " + Arrays.toString(args));
		} else if (args[0].equalsIgnoreCase("-checkReminders")) {
			logger.info("Programmatically checking for reminders...");
			return true;
		}
		logger.fatal("Invalid arg passed in first position: " + args[0]);
		return false;
	}

	private static void runApp() throws GeneralSecurityException,
			IOException {
		// Set custom look and feel if there is one
		setLookAndFeel();

		// Show initial Loading GUI
		new Loading();

		// Set security based on if it was launched from the dev workspace
		if (ApplicationLiterals.isFromWorkspace()) {
			USER_VALIDATION = false;
		}

		if (USER_VALIDATION) {
			VerifyAccess.CheckAccess();
		} else {
			logger.debug("Skipping authentication - working from dev workspace");
			Connect.InitialConnect("ROOT");
		}
	}

	private static void setLookAndFeel() {
		String themeFileName = ReadConfig
				.getConfigValue(ApplicationLiterals.THEME_FILE_NAME);
		File themeFile = new File(ApplicationLiterals.THEME_DIR
				+ ApplicationLiterals.SLASH + themeFileName);
		if (themeFile.exists()) {
			try {
				logger.debug("Default theme file found");
				BufferedReader br = new BufferedReader(
						new FileReader(themeFile));
				String line = br.readLine();
				UIManager.setLookAndFeel(line);
				logger.debug("Theme successfully set to class: "
						+ line.substring(line
								.lastIndexOf(ApplicationLiterals.DOT) + 1));
				br.close();
			} catch (Exception e) {
				logger.error("Error setting theme:" + e.toString()
						+ Arrays.toString(e.getStackTrace()));
			}
		}
	}
}

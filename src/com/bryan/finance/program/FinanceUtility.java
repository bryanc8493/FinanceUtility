package com.bryan.finance.program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.database.Connect;
import com.bryan.finance.gui.VerifyAccess;
import com.bryan.finance.gui.util.Loading;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.logging.AppLogger;

/**
 * Application Entry Point
 *
 * left off - added savings view with Foreign Key - now need to fix issue when new tran gets added
 * it needs to know its a savings tran, add it to the savings table, then also check the category,
 * if it is "house savings" then we need add that new savings record tran id as a the FK in a new record in the house_savings table
 *
 * Once done with that - you then should do further verification on properly updating the savings table when adding expenses and incomes
 *
 * Lastly, check on the monthly update to see if the march run will work - some work may be needed around this.
 */
public class FinanceUtility {

	private static boolean USER_VALIDATION = true;
	private static Logger logger;
	public static AppLogger appLogger;

	public static void main(String[] args) throws GeneralSecurityException,
			IOException {
		appLogger = new AppLogger();
		logger = Logger.getLogger(FinanceUtility.class);

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

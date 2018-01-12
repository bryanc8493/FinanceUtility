package com.bryan.finance.literals;

import java.awt.Color;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.text.NumberFormatter;

import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.program.FinanceUtility;

public abstract class ApplicationLiterals {

	public static final String NEW_LINE = "\n";
	public static final String DOT_CSV = ".csv";
	public static final String UNDERSCORE = "_";
	public static final String COMMA = ",";
	public static final String SLASH = "/";
	public static final String DOUBLE_SLASH = "//";
	public static final char DOT = '.';
	public static final String BIN = "bin";
	public static final String EMPTY = "";
	public static final String SEMI_COLON = ";";
	public static final String TAB = "\t";
	public static final String DOLLAR = "$";
	public static final String DASH = "-";
	public static final String SPACE = " ";

	public static final String APP_TITLE = "Finance Utility";
	public static final String VERSION = "0.0.1";

	private static final String ROOT_PASSWORD = "RootPassword";
	private static final String ENCRYPTION_KEY = "EncryptionKey";

	public static final String USER_DIR = "user.dir";
	public static final String USER_NAME = "user.name";
	public static final String THEME_DIR = getLaunchPath() + SLASH + "theme";

	public static final Color APP_COLOR = new Color(26, 88, 127);

	public static final String LOG_PROPERTY_FILE = SLASH + "config" + SLASH
			+ "log4j.properties";
	public static final Border PADDED_SPACE = BorderFactory.createEmptyBorder(
			10, 25, 15, 25);
	public static final String FIDELITY = "FIDELITY";
	public static final String JANUS = "JANUS";

	public static final String EXPENSE = "Expense";
	public static final String INCOME = "Income";

	public static final String LOCK = "lock";
	public static final String LOCKED = "LOCKED";
	public static final String UNLOCK = "unlock";
	public static final String UNLOCKED = "UNLOCKED";

	/*
	 * Config literals
	 */
	public static final String DB_URL = "ConnectionURL";
	public static final String MY_SQL_CLASS = "MySQLClassName";
	public static final String DB_USER = "DBUsername";
	public static final String DB_PASS = "DBPassword";
	public static final String DB_PORT = "DBPort";
	public static final String MY_SQL_DIR = "MySQLDirectory";
	public static final String MY_SQL_BACKUP = "MySQLBackupLocation";
	public static final String EXPENSE_CATEGORIES = "ExpenseCategories";
	public static final String INCOME_CATEGORIES = "IncomeCategories";
	public static final String SAVINGS_SAFE_AMT = "SavingsSafeAmount";
	public static final String VIEWING_AMOUNT_MAX = "ViewingRecords";
	public static final String HTML_TEMPLATE = "HTMLTemplateFile";
	public static final String CHART_OUTPUT = "ChartOutputFile";
	public static final String REPORTS_OUTPUT_DIR = "ReportsOutputDirectory";
	public static final String DEVELOPMENT_WORKSPACE = "DevelopmentWorkspace";
	public static final String THEME_FILE_NAME = "ThemeFileName";
	public static final String REPORT_TYPES = "ReportTypes";
	public static final String DEFAULT_PASSWORD = "DefaultPassword";
	public static final String ADMINISTRATOR = "Administrator";

	/*
	 * Dates
	 */
	public static final SimpleDateFormat YEAR_MONTH_DAY = new SimpleDateFormat(
			"yyyy-MM-dd");
	public static final SimpleDateFormat YEAR_MONTH = new SimpleDateFormat(
			"yyyy-MM");
	public static final SimpleDateFormat YEAR = new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat MONTH = new SimpleDateFormat("M");
	public static final SimpleDateFormat FULL_DATE = new SimpleDateFormat(
			"EEEE, MMM d  h:mm:ss a");
	public static final SimpleDateFormat YEAR_MONTH_DAY_CONDENSED = new SimpleDateFormat(
			"yyyyMMdd");

	/*
	 * States
	 */
	public final static String[] STATE_CODES = { "AL", "AK", "AZ", "AR", "CA",
			"CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS",
			"KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE",
			"NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA",
			"RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI",
			"WY" };

	public static final String LOCAL_WORKSPACE = ReadConfig
			.getConfigValue(DEVELOPMENT_WORKSPACE);

	public static String getLaunchPath() {
		String path = FinanceUtility.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath();
		if (path.contains(".jar")) {
			path = path.replace("FinanceUtility.jar", "");
		}
		return path;
	}

	public static boolean isFromWorkspace() {
		String startDir = System.getProperty(ApplicationLiterals.USER_DIR);
		return startDir.equalsIgnoreCase(LOCAL_WORKSPACE);
	}

	public static NumberFormat getNumberFormat() {
		NumberFormat nf = NumberFormat.getInstance();
		nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		return nf;
	}

	public static String getRootPassword() {
		return ReadConfig.getConfigValue(ROOT_PASSWORD);
	}

	public static String getEncryptionKey() {
		return ReadConfig.getConfigValue(ENCRYPTION_KEY);
	}

	public static NumberFormatter getCurrencyFormat() {
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setMinimum(0.0);
		formatter.setMaximum(10000000.0);
		formatter.setAllowsInvalid(false);
		return formatter;
	}
}

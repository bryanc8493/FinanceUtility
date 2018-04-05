package com.bryan.finance.gui.finance;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import javax.swing.*;

import com.bryan.finance.database.queries.Transactions;
import com.bryan.finance.gui.util.PromptComboBoxRenderer;
import com.bryan.finance.utilities.HintTextField;
import org.apache.log4j.Logger;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.bryan.finance.beans.Transaction;
import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.database.Connect;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.MainMenu;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.program.FinanceUtility;
import com.bryan.finance.utilities.DateLabelFormatter;

public class InsertTransaction {

	private static Logger logger = Logger.getLogger(InsertTransaction.class);
	private static String[] EXPENSE_CATEGORIES;
	private static String[] INCOME_CATEGORIES;
	private static JComboBox<String> selectCategory = new JComboBox<>();

	private final static JTextField descField = new HintTextField("Description", false, 12);
	private final static JTextField storeField = new HintTextField("Store", false, 12);
	private final static JTextField titleField = new HintTextField("Transaction Title", false, 12);
	private final static JFormattedTextField amountField = new JFormattedTextField(
			ApplicationLiterals.getCurrencyFormat());

	public static void InsertFrame() {
		logger.debug("Displaying GUI to insert new transaction");
		final Connection con = Connect.getConnection();

		EXPENSE_CATEGORIES = getCategories(ApplicationLiterals.EXPENSE);
		INCOME_CATEGORIES = getCategories(ApplicationLiterals.INCOME);
		final String[] TYPE_CATEGORIES = { "Expense", "Income" };

		final JFrame frame = new JFrame(ApplicationLiterals.APP_TITLE);

		final JComboBox<String> typeCb = new JComboBox<>(TYPE_CATEGORIES);
		typeCb.setFont(ApplicationLiterals.APP_FONT);
		typeCb.setSelectedIndex(-1);
		typeCb.setRenderer(new PromptComboBoxRenderer("Select Type"));
		typeCb.setPrototypeDisplayValue(" temp prototype value ");

		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		final JDatePickerImpl datePicker = new JDatePickerImpl(datePanel,
				new DateLabelFormatter());
		model.setValue(new Date());
		model.setSelected(true);

		amountField.setColumns(10);
		amountField.setValue(0.0);
		amountField.setFont(ApplicationLiterals.APP_FONT);

		final JCheckBox credit = new JCheckBox("  Credit");

		final JButton insert = new PrimaryButton("    Insert    ");
		final JButton back = new PrimaryButton("    < Back    ");
		final JButton close = new PrimaryButton("    Close    ");

		final JLabel missingField = new JLabel(
				"Please fill in all fields with a *");
		missingField.setForeground(Color.RED);
		missingField.setVisible(false);

		JPanel details = new JPanel(new BorderLayout(0,10));
		details.add(titleField, BorderLayout.NORTH);
		details.add(descField, BorderLayout.CENTER);
		details.add(storeField, BorderLayout.SOUTH);
		details.setBorder(BorderFactory.createCompoundBorder(
				ApplicationLiterals.PADDED_SPACE,
				BorderFactory.createTitledBorder("General Details:")));

		JPanel typeAndCateogry = new JPanel(new BorderLayout(0, 10));
		selectCategory.setPrototypeDisplayValue(" temp prototype value ");
		typeAndCateogry.add(typeCb, BorderLayout.NORTH);
		typeAndCateogry.add(selectCategory, BorderLayout.SOUTH);
		typeAndCateogry.setBorder(BorderFactory.createCompoundBorder(
				ApplicationLiterals.PADDED_SPACE,
				BorderFactory.createTitledBorder("Type and Category:")));

		JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
		top.add(details);
		top.add(typeAndCateogry);

		JPanel amountDetails = new JPanel(new FlowLayout(FlowLayout.LEFT));
		amountDetails.add(datePicker);
		amountDetails.add(amountField);
		amountDetails.add(credit);
		amountDetails.setBorder(BorderFactory.createCompoundBorder(
				ApplicationLiterals.PADDED_SPACE,
				BorderFactory.createTitledBorder("Amount and Date")));

		JPanel content = new JPanel(new BorderLayout(0, 5));
		content.add(top, BorderLayout.NORTH);
		content.add(amountDetails, BorderLayout.SOUTH);

		JPanel missing = new JPanel();
		missing.setLayout(new FlowLayout(FlowLayout.CENTER));
		missing.add(missingField);
		JPanel middle = new JPanel();
		middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
		middle.add(content);
		middle.add(missing);

		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttons.add(back);
		buttons.add(close);
		buttons.add(insert);

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		JLabel frameTitle = new Title("Insert Transaction");
		main.add(frameTitle, BorderLayout.NORTH);
		main.add(middle, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.SOUTH);

		frame.add(main);
		frame.setIconImage(Icons.APP_ICON.getImage());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		JRootPane rp = SwingUtilities.getRootPane(insert);
		rp.setDefaultButton(insert);
		rp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		close.addActionListener(e -> {
			frame.dispose();
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {
					throw new AppException(e1);
				}
			}
			logger.info("Closed by user");
			FinanceUtility.appLogger.logFooter();
			System.exit(0);
		});

		back.addActionListener(e -> {
			frame.dispose();
			MainMenu.modeSelection(false, 0);
		});

		selectCategory.addActionListener(e -> {
			try {
				if (selectCategory.getSelectedItem().toString().equalsIgnoreCase("Credit Card")) {
					int choice = JOptionPane.showConfirmDialog(null,
							"Would you like to pay existing outstanding credit charges?", "Confirm",
							JOptionPane.YES_NO_OPTION);
					if (choice == JOptionPane.YES_OPTION) {
						new CreditPayments();
						resetDefaults();
					}
				}
			} catch (Exception ex) { }
		});

		typeCb.addActionListener(e -> {
			if (typeCb.getSelectedItem().toString()
					.equalsIgnoreCase(ApplicationLiterals.EXPENSE)) {
				addCategories(true);
				storeField.setVisible(true);
				credit.setVisible(true);
			} else if (typeCb.getSelectedItem().toString()
					.equalsIgnoreCase(ApplicationLiterals.INCOME)) {
				addCategories(false);
				storeField.setVisible(false);
				credit.setSelected(false);
				credit.setVisible(false);
			}
		});

		insert.addActionListener(e -> {
			// First will check if any required fields are not filled in
			String input_title = titleField.getText().trim();
			String input_amount = amountField
					.getText()
					.replace(ApplicationLiterals.DOLLAR,
							ApplicationLiterals.EMPTY).trim();
			input_amount = input_amount.replace(ApplicationLiterals.COMMA,
					ApplicationLiterals.EMPTY);
			Date selectedDate = (Date) datePicker.getModel().getValue();
			String input_trans_date = ApplicationLiterals.YEAR_MONTH_DAY
					.format(selectedDate);

			// Second will check if the date is in the correct format
			if (input_trans_date.length() != 10) {
				logger.warn("Date not in correct format, must be 10 total characters: 'YYYY-MM-DD'");
			} else if (input_title.equals(ApplicationLiterals.EMPTY)
					|| input_amount.equals("0.00")
					|| input_trans_date
							.contains(ApplicationLiterals.UNDERSCORE)
					|| typeCb.getSelectedItem().toString()
							.equals(ApplicationLiterals.EMPTY)) {
				logger.warn("Missing some required fields");
				missingField.setVisible(true);
				frame.pack();
			}

			else {
				// Check that transaction type is selected (i.e. expense or
				// income) as well as category
				try {
					typeCb.getSelectedItem().toString();
					selectCategory.getSelectedItem().toString();
				} catch (NullPointerException ex) {
					logger.warn("Income or expense must be selected as well as a category!");
					missingField.setVisible(true);
					frame.pack();
					return;
				}

				// Will continue and run this if all required fields are
				// properly filled in
				Transaction tran = new Transaction();
				tran.setTitle(input_title);
				tran.setCategory(selectCategory.getSelectedItem()
						.toString());

				if (typeCb.getSelectedItem().toString()
						.equalsIgnoreCase(ApplicationLiterals.EXPENSE)) {
					tran.setType(ApplicationLiterals.EXPENSE);
					tran.setCombinedAmount(ApplicationLiterals.DASH
							+ input_amount);
				} else if (typeCb.getSelectedItem().toString()
						.equalsIgnoreCase(ApplicationLiterals.INCOME)) {
					tran.setType(ApplicationLiterals.INCOME);
					tran.setCombinedAmount(input_amount);
				}

				tran.setDate(input_trans_date);
				tran.setAmount(input_amount);
				tran.setDescription(descField.getText());
				char creditFlag = credit.isSelected() ? '1' : '0';
				tran.setCredit(creditFlag);
				if (creditFlag == '1') {
					tran.setCreditPaid('0');
				}

				// Call method to run query, pass all selected criteria
				Transactions.addTransaction(tran);

				resetDefaults();
			}
		});
	}

	private static void resetDefaults() {
		titleField.setText(ApplicationLiterals.EMPTY);
		selectCategory.setSelectedIndex(-1);
		amountField.setText("0.00");
		amountField.setCaretPosition(1);
		descField.setText(ApplicationLiterals.EMPTY);
		storeField.setText(ApplicationLiterals.EMPTY);
		titleField.requestFocusInWindow();
	}

	private static String[] getCategories(String type) {
		String rawExpCategories = ReadConfig
				.getConfigValue(ApplicationLiterals.EXPENSE_CATEGORIES);
		String rawIncCategories = ReadConfig
				.getConfigValue(ApplicationLiterals.INCOME_CATEGORIES);

		String categoryData = type.equals(ApplicationLiterals.EXPENSE) ? rawExpCategories : rawIncCategories;

		String[] categories = categoryData.split(ApplicationLiterals.COMMA);
		for (int i = 0; i < categories.length; i++) {
			categories[i] = categories[i].trim();
		}
		return categories;
	}

	private static void addCategories(boolean isExpenses) {

		selectCategory.removeAllItems();
		String[] categories = isExpenses ? EXPENSE_CATEGORIES
				: INCOME_CATEGORIES;
		for (String c : categories) {
			selectCategory.addItem(c);
		}
		selectCategory.setFont(ApplicationLiterals.APP_FONT);
		selectCategory.setRenderer(new PromptComboBoxRenderer("Select Category"));
		selectCategory.setSelectedIndex(-1);
	}
}

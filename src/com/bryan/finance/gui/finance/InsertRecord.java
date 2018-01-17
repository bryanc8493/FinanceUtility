package com.bryan.finance.gui.finance;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.bryan.finance.beans.Transaction;
import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.database.Connect;
import com.bryan.finance.database.InsertExpense;
import com.bryan.finance.database.InsertIncome;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.MainMenu;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.program.FinanceUtility;
import com.bryan.finance.utilities.DateLabelFormatter;

public class InsertRecord {

	private static Logger logger = Logger.getLogger(InsertRecord.class);
	private static String[] EXPENSE_CATEGORIES;
	private static String[] INCOME_CATEGORIES;
	private static JComboBox<String> selectCategory = new JComboBox<>();

	public static void InsertFrame() {
		logger.debug("Displaying GUI to insert new transaction");
		final Connection con = Connect.getConnection();
		String rawExpCategories = ReadConfig
				.getConfigValue(ApplicationLiterals.EXPENSE_CATEGORIES);
		String rawIncCategories = ReadConfig
				.getConfigValue(ApplicationLiterals.INCOME_CATEGORIES);
		EXPENSE_CATEGORIES = getCategories(rawExpCategories);
		INCOME_CATEGORIES = getCategories(rawIncCategories);
		final String[] TYPE_CATEGORIES = { "Expense", "Income" };

		final JFrame frame = new JFrame(ApplicationLiterals.APP_TITLE);

		JLabel title = new JLabel("* Title");
		final JTextField titleField = new JTextField();

		// Type selection (income or expense)
		JLabel type = new JLabel("* Type");
		final JComboBox<String> typeCb = new JComboBox<>(TYPE_CATEGORIES);
		typeCb.setSelectedItem(null);

		// Category Selection
		final JLabel category = new JLabel("* Category");

		// Date Selection
		JLabel transDate = new JLabel("* Date of Transaction");

		// Date picker
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

		// Amount selection
		JLabel amount = new JLabel("* Amount");
		final JFormattedTextField amountField = new JFormattedTextField(
				ApplicationLiterals.getCurrencyFormat());
		amountField.setColumns(10);
		amountField.setValue(0.0);

		// Description input
		JLabel desc = new JLabel("  Description");
		final JTextField descField = new JTextField();

		// Store input
		final JLabel store = new JLabel("  Store");
		final JTextField storeField = new JTextField();

		// Credit input
		final JCheckBox credit = new JCheckBox("  Credit");

		// Buttons
		final JButton insert = new PrimaryButton("    Insert    ");
		final JButton back = new PrimaryButton("    < Back    ");
		final JButton close = new PrimaryButton("    Close    ");

		final JLabel missingField = new JLabel(
				"Please fill in all fields with a *");
		missingField.setForeground(Color.RED);
		missingField.setVisible(false);

		JPanel grid = new JPanel();
		grid.setLayout(new GridLayout(8, 2, 5, 10));
		grid.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
		grid.add(title);
		grid.add(titleField);
		grid.add(type);
		grid.add(typeCb);
		grid.add(category);
		grid.add(selectCategory);
		grid.add(transDate);
		grid.add(datePicker);
		grid.add(amount);
		grid.add(amountField);
		grid.add(desc);
		grid.add(descField);
		grid.add(store);
		grid.add(storeField);
		grid.add(new JLabel());
		grid.add(credit);

		JPanel missing = new JPanel();
		missing.setLayout(new FlowLayout(FlowLayout.CENTER));
		missing.add(missingField);

		JPanel middle = new JPanel();
		middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
		middle.add(grid);
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

		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
			}
		});

		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				MainMenu.modeSelection(false, 0);
			}
		});

		typeCb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (typeCb.getSelectedItem().toString()
						.equalsIgnoreCase(ApplicationLiterals.EXPENSE)) {
					addCategories(true);
					store.setVisible(true);
					storeField.setVisible(true);
					credit.setVisible(true);
				} else if (typeCb.getSelectedItem().toString()
						.equalsIgnoreCase(ApplicationLiterals.INCOME)) {
					addCategories(false);
					store.setVisible(false);
					storeField.setVisible(false);
					credit.setSelected(false);
					credit.setVisible(false);
				}
			}
		});
		insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
					if (typeCb.getSelectedItem().toString()
							.equalsIgnoreCase(ApplicationLiterals.EXPENSE)) {
						tran.setStore(storeField.getText());
						// Before running, check if the selected category was
						// credit card payment, if yes then first show
						// CreditPayments
						if (tran.getCategory().equalsIgnoreCase("Credit Card")) {
							new CreditPayments();
						} else {
							InsertExpense.NewExpense(tran, con);
						}
					} else if (typeCb.getSelectedItem().toString()
							.equalsIgnoreCase(ApplicationLiterals.INCOME)) {
						InsertIncome.NewIncome(tran, con);
					}

					// Reset GUI objects
					titleField.setText(ApplicationLiterals.EMPTY);
					selectCategory.setSelectedItem(null);
					amountField.setText("0.00");
					amountField.setCaretPosition(1);
					descField.setText(ApplicationLiterals.EMPTY);
					storeField.setText(ApplicationLiterals.EMPTY);
					titleField.requestFocusInWindow();
				}
			}
		});
	}

	private static String[] getCategories(String str) {

		String[] data = str.split(ApplicationLiterals.COMMA);
		for (int i = 0; i < data.length; i++) {
			data[i] = data[i].trim();
		}
		return data;
	}

	private static void addCategories(boolean isExpenses) {

		selectCategory.removeAllItems();
		String[] categories = isExpenses ? EXPENSE_CATEGORIES
				: INCOME_CATEGORIES;
		for (String c : categories) {
			selectCategory.addItem(c);
		}
	}
}

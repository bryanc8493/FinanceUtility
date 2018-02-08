package com.bryan.finance.gui.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.apache.log4j.Logger;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.bryan.finance.beans.CategorySummary;
import com.bryan.finance.beans.ReportRecord;
import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.database.Connect;
import com.bryan.finance.database.GenerateReport;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.MainMenu;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.program.FinanceUtility;
import com.bryan.finance.reporting.WriteReport;
import com.bryan.finance.utilities.DateLabelFormatter;

public class CustomReport {

	private static final int MIN_YEAR = 2016;
	private static final ImageIcon appIcon = Icons.APP_ICON;
	private static Logger logger = Logger.getLogger(CustomReport.class);

	private static final String[] reports = getReportTypes();
	private static final String[] outputs = { "HTML Chart", "CSV File",
			"Text File" };

	private static JComboBox<String> reportCB;
	private static JComboBox<String> outputCB;
	private static JComboBox<Integer> years;
	private static JComboBox<Integer> months;

	private static JRadioButton monthly;
	private static JRadioButton custom;

	private static JDatePickerImpl datePicker;
	private static JDatePickerImpl datePickerTwo;

	public static void selectReport() {
		logger.debug("Displaying Reports Frame");
		final Connection con = Connect.getConnection();
		final JFrame frame = new JFrame("Reporting");
		JLabel title = new Title("Select Report Details");

		reportCB = new JComboBox<>(reports);
		outputCB = new JComboBox<>(outputs);
		reportCB.setSelectedItem(null);
		outputCB.setSelectedItem(null);

		monthly = new JRadioButton("Monthly Report");
		custom = new JRadioButton("Custom Range");
		JLabel monthsLabel = new JLabel("Month:");
		JLabel yearLabel = new JLabel("Year:");

		years = new JComboBox<>(getValidYears());
		months = new JComboBox<>(getMonths());
		months.setSelectedIndex(getLastMonth());
		months.setMaximumRowCount(12);

		JButton back = new PrimaryButton("< Back");
		JButton close = new PrimaryButton("Close");
		JButton run = new PrimaryButton("Run");

		ButtonGroup group = new ButtonGroup();
		group.add(monthly);
		group.add(custom);

		JPanel reportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		reportPanel.add(new JLabel("Select Report Type:"));
		reportPanel.add(reportCB);

		JPanel outputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		outputPanel.add(new JLabel("Select Output Type:"));
		outputPanel.add(outputCB);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		buttons.add(back);
		buttons.add(close);
		buttons.add(run);

		JPanel selection = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		selection.add(monthly);
		selection.add(custom);

		final JPanel monthSelect = new JPanel(new FlowLayout(FlowLayout.CENTER));
		monthSelect.add(yearLabel);
		monthSelect.add(years);
		monthSelect.add(monthsLabel);
		monthSelect.add(months);
		monthSelect.setVisible(false);

		// Date picker
		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

		// Date picker
		UtilDateModel modelTwo = new UtilDateModel();
		Properties prop = new Properties();
		prop.put("text.today", "Today");
		prop.put("text.month", "Month");
		prop.put("text.year", "Year");
		JDatePanelImpl datePanelTwo = new JDatePanelImpl(modelTwo, prop);
		datePickerTwo = new JDatePickerImpl(datePanelTwo,
				new DateLabelFormatter());

		final JPanel dates = new JPanel(new FlowLayout(FlowLayout.CENTER));
		dates.add(new JLabel("Start"));
		dates.add(datePicker);
		dates.add(new JLabel("End"));
		dates.add(datePickerTwo);
		dates.setVisible(false);

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.add(Box.createRigidArea(new Dimension(0, 15)));
		content.add(reportPanel);
		content.add(Box.createRigidArea(new Dimension(0, 10)));
		content.add(new JSeparator(JSeparator.HORIZONTAL));
		content.add(Box.createRigidArea(new Dimension(0, 10)));
		content.add(selection);
		content.add(dates);
		content.add(Box.createRigidArea(new Dimension(0, 10)));
		content.add(monthSelect);
		content.add(Box.createRigidArea(new Dimension(0, 10)));
		content.add(new JSeparator(JSeparator.HORIZONTAL));
		content.add(Box.createRigidArea(new Dimension(0, 10)));
		content.add(outputPanel);
		Border space = BorderFactory.createEmptyBorder(10, 25, 15, 25);
		content.setBorder(BorderFactory.createCompoundBorder(space,
				BorderFactory.createTitledBorder("Date (Year & Month):")));

		JPanel main = new JPanel(new BorderLayout());
		main.add(title, BorderLayout.NORTH);
		main.add(content, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.SOUTH);

		frame.add(main);
		frame.setIconImage(appIcon.getImage());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		JRootPane rp = SwingUtilities.getRootPane(frame);
		rp.setDefaultButton(run);
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

		monthly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (monthly.isSelected()) {
					monthSelect.setVisible(true);
					dates.setVisible(false);
					frame.pack();
					frame.setLocationRelativeTo(null);
				}
			}
		});

		reportCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (reportCB.getSelectedIndex() == 0) {
					outputCB.setEnabled(false);
					outputCB.setSelectedItem(null);
					monthly.setEnabled(true);
				} else if (reportCB.getSelectedIndex() == 1) {
					custom.setSelected(true);
					monthSelect.setVisible(false);
					dates.setVisible(true);
					monthly.setEnabled(false);
					outputCB.setEnabled(true);
					frame.pack();
					frame.setLocationRelativeTo(null);
				} else {
					monthly.setEnabled(true);
					outputCB.setEnabled(true);
					frame.pack();
				}
			}
		});

		custom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (custom.isSelected()) {
					monthSelect.setVisible(false);
					dates.setVisible(true);
					frame.pack();
				}
			}
		});

		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Verify input
				if (!isValidInput()) {
					JOptionPane.showMessageDialog(frame,
							"Please fill in all selections", "Invalid Input",
							JOptionPane.WARNING_MESSAGE);
				} else {
					// run differently based on run type
					if (reportCB.getSelectedIndex() == 0) {
						runMonthlyUpdates();
					} else if (reportCB.getSelectedIndex() == 1) {
						runExpensesByCategory();
					} else if (reportCB.getSelectedIndex() == 2) {
						runJanusPerformance();
					} else {
						runFidelityPerformance();
					}
				}
			}
		});

	}

	private static void runFidelityPerformance() {

	}

	private static void runJanusPerformance() {

	}

	private static void runExpensesByCategory() {
		Date selectedBeginDate = (Date) datePicker.getModel().getValue();
		String startDate = ApplicationLiterals.YEAR_MONTH
				.format(selectedBeginDate);

		Date selectedEndDate = (Date) datePickerTwo.getModel().getValue();
		String endDate = ApplicationLiterals.YEAR_MONTH.format(selectedEndDate);
		logger.info("Running custom report for range: " + startDate + " to "
				+ endDate);

		// Query DB and get report data
		Map<String, List<CategorySummary>> data = GenerateReport
				.categoryReportT(startDate, endDate);

		for (Map.Entry<String, List<CategorySummary>> m : data.entrySet()) {
			System.out.println("DATE: " + m.getKey() + ApplicationLiterals.TAB
					+ "VALUE: " + printList(m.getValue()));
		}

		// use data and html template to generate chart
		GenerateReport.createCategoryChart(data);
	}

	private static String printList(List<CategorySummary> x) {
		String r = "";
		int i = 0;
		for (CategorySummary c : x) {
			if (i != 0) {
				r = r + " || " + c.getCategory() + "=" + c.getAmount();
			} else {
				r = c.getCategory() + "=" + c.getAmount();
			}
			i++;
		}
		return r;
	}

	private static void runMonthlyUpdates() {
		if (monthly.isSelected()) {
			int year = (int) years.getSelectedItem();
			int month = (int) months.getSelectedItem();
			logger.info("Running monthly report for: " + month + "/" + year);

			// Query DB and get report Data records
			GenerateReport.createMonthlyReport(year, month);

			JOptionPane.showMessageDialog(null,
					"Complete!\nData was updated for " + month + "-" + year,
					"Success", JOptionPane.INFORMATION_MESSAGE);
		} else {
			Date selectedBeginDate = (Date) datePicker.getModel().getValue();
			String startDate = ApplicationLiterals.YEAR_MONTH_DAY
					.format(selectedBeginDate);

			Date selectedEndDate = (Date) datePickerTwo.getModel().getValue();
			String endDate = ApplicationLiterals.YEAR_MONTH_DAY
					.format(selectedEndDate);
			logger.info("Running custom report for range: " + startDate
					+ " to " + endDate);

			// Query DB and get report data
			List<ReportRecord> data = GenerateReport.createCustomReport(
					startDate, endDate);

			// Generate csv report if it was selected
			String path = WriteReport.createCSVOutput(data, startDate, endDate);
			JOptionPane.showMessageDialog(null, "Report was written to:\n"
					+ path, "Complete", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	private static Integer[] getValidYears() {
		String currentYearString = ApplicationLiterals.YEAR.format(new Date());
		int currentYear = Integer.parseInt(currentYearString);
		int yearsBetween = currentYear - MIN_YEAR;
		Integer[] years = new Integer[yearsBetween+1];

		int c = 0;
		for(int i=MIN_YEAR; i<currentYear+1; i++) {
			years[c] = i;
			c++;
		}
		return years;
	}

	private static int getLastMonth() {
		String monthString = ApplicationLiterals.MONTH.format(new Date());
		int currentMonth = Integer.parseInt(monthString);

		if (currentMonth == 1) {
			currentMonth = 11;
		} else {
			currentMonth = currentMonth - 2;
		}
		return currentMonth;
	}

	private static Integer[] getMonths() {
		Integer[] months = new Integer[12];
		for (int i = 0; i < months.length; i++) {
			months[i] = i + 1;
		}
		return months;
	}

	private static boolean isValidInput() {
		boolean valid = true;

		if (!monthly.isSelected() && !custom.isSelected()) {
			valid = false;
		}

		if (reportCB.getSelectedItem() == null) {
			valid = false;
		}

		if (outputCB.getSelectedItem() == null
				&& reportCB.getSelectedIndex() != 0) {
			valid = false;
		}

		return valid;
	}

	private static String[] getReportTypes() {
		String types = ReadConfig
				.getConfigValue(ApplicationLiterals.REPORT_TYPES);
		return types.split(ApplicationLiterals.COMMA);
	}
}

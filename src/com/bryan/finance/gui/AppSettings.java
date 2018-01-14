package com.bryan.finance.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;

public class AppSettings extends ApplicationLiterals {

	private JLabel expenseCategoriesLabel = new JLabel("Expense Categories");
	private JLabel incomeCategoriesLabel = new JLabel("Income Categories");
	private JLabel savingsSafeAmtLabel = new JLabel("Savings Emergency Amount");
	private JLabel viewingRecordsLabel = new JLabel("Last Viewing Records");
	private JLabel htmlTemplateLabel = new JLabel("HTML Template File");
	private JLabel chartOutputLabel = new JLabel("HTML Chart Output File");

	private JTextField expenseCategories = new JTextField(30);
	private JTextField incomeCategories = new JTextField(30);
	private JTextField savingsSafeAmt = new JTextField(30);
	private JTextField viewingRecords = new JTextField(30);
	private JTextField htmlTemplate = new JTextField(30);
	private JTextField chartOutput = new JTextField(30);

	private JButton update = new PrimaryButton("Apply & Save");

	public AppSettings(boolean isModifable) {
		final JFrame frame = new JFrame("Application Settings");

		JLabel title = new Title("Current Application Settings");

		Map<String, String> props = ReadConfig.getAllProperties();
		setCurrentAppSettings(props);

		JPanel contentLabels = new JPanel(new GridLayout(6, 1, 10, 10));
		contentLabels.add(expenseCategoriesLabel);
		contentLabels.add(incomeCategoriesLabel);
		contentLabels.add(savingsSafeAmtLabel);
		contentLabels.add(viewingRecordsLabel);
		contentLabels.add(htmlTemplateLabel);
		contentLabels.add(chartOutputLabel);

		JPanel contentItems = new JPanel(new GridLayout(6, 1, 10, 10));
		contentItems.add(expenseCategories);
		contentItems.add(incomeCategories);
		contentItems.add(savingsSafeAmt);
		contentItems.add(viewingRecords);
		contentItems.add(htmlTemplate);
		contentItems.add(chartOutput);

		JPanel content = new JPanel(new BorderLayout(20, 0));
		content.add(contentLabels, BorderLayout.WEST);
		content.add(contentItems, BorderLayout.EAST);
		content.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

		JButton close = new PrimaryButton("Close");

		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottom.add(update);
		update.setVisible(isModifable);
		bottom.add(close);

		JPanel main = new JPanel(new BorderLayout());
		main.add(title, BorderLayout.NORTH);
		main.add(content, BorderLayout.CENTER);
		main.add(bottom, BorderLayout.SOUTH);

		frame.add(main);
		frame.setIconImage(Icons.APP_ICON.getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JRootPane rp = SwingUtilities.getRootPane(close);
		rp.setDefaultButton(close);
		rp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});

		update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateProperties();
				frame.dispose();
			}
		});
	}

	private void setCurrentAppSettings(Map<String, String> props) {
		expenseCategories.setText(props.get(EXPENSE_CATEGORIES));
		incomeCategories.setText(props.get(INCOME_CATEGORIES));
		savingsSafeAmt.setText(props.get(SAVINGS_SAFE_AMT));
		viewingRecords.setText(props.get(VIEWING_AMOUNT_MAX));
		htmlTemplate.setText(props.get(HTML_TEMPLATE));
		chartOutput.setText(props.get(CHART_OUTPUT));
	}

	private void updateProperties() {
		String configFile = ReadConfig.getConfigFile(getLaunchPath());
		configFile = configFile.replace("bin/", ApplicationLiterals.EMPTY)
				.replace(ApplicationLiterals.DOUBLE_SLASH,
						ApplicationLiterals.SLASH);

		try {
			PropertiesConfiguration config = new PropertiesConfiguration(
					configFile);
			config.setProperty(EXPENSE_CATEGORIES, expenseCategories.getText()
					.trim());
			config.setProperty(INCOME_CATEGORIES, incomeCategories.getText()
					.trim());
			config.setProperty(SAVINGS_SAFE_AMT, savingsSafeAmt.getText()
					.trim());
			config.setProperty(VIEWING_AMOUNT_MAX, viewingRecords.getText()
					.trim());
			config.setProperty(HTML_TEMPLATE, htmlTemplate.getText().trim());
			config.setProperty(CHART_OUTPUT, chartOutput.getText().trim());
			config.save();

			JOptionPane.showMessageDialog(null,
					"Settings Updated Successfully!", "Complete",
					JOptionPane.INFORMATION_MESSAGE);
		} catch (ConfigurationException e) {
			new AppException(e);
		}
	}
}

package com.bryan.finance.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Set;
import javax.swing.text.Document;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.bryan.finance.beans.Salary;
import com.bryan.finance.database.queries.QueryUtil;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.utilities.HintTextField;
import com.bryan.finance.utilities.SimpleDocumentListener;
import org.apache.log4j.Logger;

public class SalaryManagement implements ActionListener {

	private Logger logger = Logger.getLogger(SalaryManagement.class);
	private JFrame frame = new JFrame("Salaries");
	private Set<Salary> salaries;
	private NumberFormat decimal = ApplicationLiterals.getNumberFormat();

	private final Double BONUS_MINIMUM = 0.0;
	private final Double BONUS_MAXIMUM = 200.0;
	private final Double COMP_RATIO_MIN = 80.0;
	private final Double COMP_RATIO_MAX = 120.0;

	// Input components
	private JComboBox<Integer> jobGrade = new JComboBox<>();
	private JTextField compRatio = new HintTextField("80% - 120%", false);
	private JTextField STIPerf = new HintTextField("0% - 200%", false);
	private JTextField MTIPerf = new HintTextField("0% - 200%", false);

	// Output components
	private JLabel basePay = new JLabel();
	private JLabel monthlyPay = new JLabel();
	private JLabel biWeeklyPay = new JLabel();
	private JLabel STIBonusPercent = new JLabel();
	private JLabel STIBonusAmt = new JLabel();
	private JLabel basePayAndSTI = new JLabel();
	private JLabel MTIBonusPercent = new JLabel();
	private JLabel MTIBonusAmt = new JLabel();
	private JLabel totalBonusAmt = new JLabel();
	private JLabel totalPayAndBonus = new JLabel();

	public SalaryManagement() {
		logger.debug("Displaying salary data");
		salaries = QueryUtil.getSalaryData();

		populateJobGrades();
		JLabel title = new Title("Salary Calculator");

		// Input labels
		JLabel jobGradeLbl = new JLabel("Job Grade:");
		JLabel compRatioLbl = new JLabel("Comp Ratio:");
		JLabel STIlbl = new JLabel("STI Performance:");
		JLabel MTIlbl = new JLabel("MTI Performance:");

		// output labels
		JLabel basePaylbl = new JLabel("Base Pay:");
		JLabel monthlyPaylbl = new JLabel("Monthly Pay:");
		JLabel biWeeklyPaylbl = new JLabel("Bi-Weekly Pay:");
		JLabel STIBonusPercentlbl = new JLabel("STI Bonus %:");
		JLabel STIBonusAmtlbl = new JLabel("STI Bonus Amount:");
		JLabel basePayAndSTIlbl = new JLabel("Base & STI Bonus:");
		JLabel MTIBonusPercentlbl = new JLabel("MTI Bonus %:");
		JLabel MTIBonusAmtlbl = new JLabel("MTI Bonus Amount:");
		JLabel totalBonusAmtlbl = new JLabel("Total Bonus Amount:");
		JLabel totalPayAndBonuslbl = new JLabel("Base & All Rewards:");

		// input grid
		JPanel input = new JPanel(new GridLayout(4, 2));
		input.add(jobGradeLbl);
		input.add(jobGrade);
		input.add(compRatioLbl);
		input.add(compRatio);
		input.add(STIlbl);
		input.add(STIPerf);
		input.add(MTIlbl);
		input.add(MTIPerf);
		Border space = BorderFactory.createEmptyBorder(10, 25, 15, 25);
		input.setBorder(BorderFactory.createCompoundBorder(space,
				BorderFactory.createTitledBorder("Input")));

		// output grid
		JPanel output = new JPanel(new GridLayout(10, 2, 0, 13));
		output.add(basePaylbl);
		output.add(basePay);
		output.add(monthlyPaylbl);
		output.add(monthlyPay);
		output.add(biWeeklyPaylbl);
		output.add(biWeeklyPay);
		output.add(STIBonusPercentlbl);
		output.add(STIBonusPercent);
		output.add(STIBonusAmtlbl);
		output.add(STIBonusAmt);
		output.add(basePayAndSTIlbl);
		output.add(basePayAndSTI);
		output.add(MTIBonusPercentlbl);
		output.add(MTIBonusPercent);
		output.add(MTIBonusAmtlbl);
		output.add(MTIBonusAmt);
		output.add(totalBonusAmtlbl);
		output.add(totalBonusAmt);
		output.add(totalPayAndBonuslbl);
		output.add(totalPayAndBonus);
		output.setBorder(BorderFactory.createCompoundBorder(space,
				BorderFactory.createTitledBorder("Output")));

		// content
		JPanel content = new JPanel(new BorderLayout());
		content.add(input, BorderLayout.NORTH);
		content.add(output, BorderLayout.SOUTH);

		// full panel
		JPanel full = new JPanel(new BorderLayout());
		full.add(title, BorderLayout.NORTH);
		full.add(content, BorderLayout.CENTER);

		frame.add(full);
		frame.setIconImage(Icons.APP_ICON.getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JRootPane rp = SwingUtilities.getRootPane(title);
		rp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		jobGrade.addActionListener(this);

		compRatio.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
			if(isValidCompRatio()) {
				setValidUI(compRatio);
				onChangeHandler();
			}
		});

		STIPerf.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
			if(isValidInput(STIPerf)) {
				setValidUI(STIPerf);
				onChangeHandler();
			}
		});

		MTIPerf.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
			if(isValidInput(MTIPerf)) {
				setValidUI(MTIPerf);
				onChangeHandler();
			}
		});
	}

	private void setInvalidUI(JTextField component) {
		component.setForeground(Color.RED);
		component.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.RED, 1),
				BorderFactory.createEmptyBorder(1,5,1,0)));
	}

	private void setValidUI(JTextField component) {
		component.setForeground(Color.BLACK);
		component.setBorder(BorderFactory.createEmptyBorder(1,5,1,0));
	}

	private boolean isValidCompRatio() {
		Double input;
		try {
			input = Double.parseDouble(compRatio.getText());
			if (input >= COMP_RATIO_MIN && input <= COMP_RATIO_MAX) {
				return true;
			}
		} catch (NumberFormatException e) {
			logger.warn("Invalid comp ratio - must be a number");
		}
		setInvalidUI(compRatio);
		return false;
	}

	private boolean isValidInput(JTextField component) {
		Double input;
		try {
			input = Double.parseDouble(component.getText());
			if (input >= BONUS_MINIMUM && input <= BONUS_MAXIMUM) {
				return true;
			}
		} catch (NumberFormatException e) {
			logger.warn("Invalid input - must be a number");
		}
		setInvalidUI(component);
		return false;
	}

	private void renderBasePay(Salary salary, Double comp) {
		basePay.setText("$ " + decimal.format(salary.getMidPay() * comp));

		monthlyPay.setText("$ "
				+ decimal.format((salary.getMidPay() * comp) / 12));

		biWeeklyPay.setText("$ "
				+ decimal.format((salary.getMidPay() * comp) / 12 / 2));
	}

	private void renderStiBonus(Double stiBonusPercentVal, Double basePayVal) {
		STIBonusPercent.setText(decimal.format(stiBonusPercentVal) + " %");

		stiBonusPercentVal = stiBonusPercentVal / 100;
		STIBonusAmt.setText("$ "
				+ decimal.format(stiBonusPercentVal * basePayVal));

		basePayAndSTI.setText("$ "
				+ decimal.format((stiBonusPercentVal * basePayVal)
				+ basePayVal));
	}

	private void renderMtiBonus(Double mtiBonusPercentVal,
								Double basePayVal, Double stiBonusPercentVal) {
		MTIBonusPercent.setText(decimal.format(mtiBonusPercentVal)
				+ " %");

		mtiBonusPercentVal = mtiBonusPercentVal / 100;
		stiBonusPercentVal = stiBonusPercentVal / 100;
		double mtiBonusAmt = mtiBonusPercentVal * basePayVal;
		MTIBonusAmt.setText("$ " + decimal.format(mtiBonusAmt));
		renderTotals(mtiBonusAmt, stiBonusPercentVal, basePayVal);
	}

	private void renderTotals(Double mtiBonusAmt, Double stiBonusPercentVal, Double basePayVal) {
		totalBonusAmt.setText("$ "
				+ decimal.format(mtiBonusAmt
				+ (stiBonusPercentVal * basePayVal)));

		double stiAndBaseAmt = (stiBonusPercentVal * basePayVal)
				+ basePayVal;
		totalPayAndBonus.setText("$ "
				+ decimal.format(mtiBonusAmt + stiAndBaseAmt));
	}

	private void onChangeHandler() {
		formatOutputLabels();
		Double comp, sti, mti;
		try {
			comp = Double.parseDouble(compRatio.getText().trim()) / 100.0;
			sti = Double.parseDouble(STIPerf.getText().trim()) / 100.0;

			Salary salary = getSelectedGradeData(Integer.parseInt(jobGrade
					.getSelectedItem().toString()));

			double basePayVal = salary.getMidPay() * comp;
			renderBasePay(salary, comp);

			double stiBonusPercentVal = salary.getStiTarget() * sti;
			renderStiBonus(stiBonusPercentVal, basePayVal);

			if (salary.getGrade() < 8) {
				MTIBonusPercent.setText("--");
				MTIBonusAmt.setText("--");
				totalBonusAmt.setText(STIBonusAmt.getText());
				totalPayAndBonus.setText(basePayAndSTI.getText());
				renderTotals(0.0, stiBonusPercentVal/100, basePayVal);
			} else {
				mti = Double.parseDouble(MTIPerf.getText().trim()) / 100.0;
				double mtiBonusPercentVal = salary.getMtiTarget() * mti;
				renderMtiBonus(mtiBonusPercentVal, basePayVal, stiBonusPercentVal);
			}
		} catch (NumberFormatException e) {
			logger.warn("parse exception during on change handler - " + e);
		}
	}

	private void populateJobGrades() {
		for (Integer i = 6; i < 29; i++) {
			if (!(i == 22 || (i > 23 & i < 28))) {
				jobGrade.addItem(i);
			}
		}
		jobGrade.setMaximumRowCount(10);
		jobGrade.setFont(ApplicationLiterals.APP_FONT);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		int selectedGrade = Integer.parseInt(jobGrade.getSelectedItem().toString());
		if (selectedGrade >= 8) {
			MTIPerf.setEnabled(true);
		}else{
			MTIPerf.setEnabled(false);
		}
	}

	private Salary getSelectedGradeData(int grade) {
		Salary salary = null;
		for (Salary s : salaries) {
			if (s.getGrade() == grade) {
				salary = s;
				break;
			}
		}
		return salary;
	}

	private void formatOutputLabels() {
		basePay.setHorizontalAlignment(SwingConstants.RIGHT);
		monthlyPay.setHorizontalAlignment(SwingConstants.RIGHT);
		biWeeklyPay.setHorizontalAlignment(SwingConstants.RIGHT);
		STIBonusPercent.setHorizontalAlignment(SwingConstants.RIGHT);
		STIBonusAmt.setHorizontalAlignment(SwingConstants.RIGHT);
		basePayAndSTI.setHorizontalAlignment(SwingConstants.RIGHT);
		MTIBonusPercent.setHorizontalAlignment(SwingConstants.RIGHT);
		MTIBonusAmt.setHorizontalAlignment(SwingConstants.RIGHT);
		totalBonusAmt.setHorizontalAlignment(SwingConstants.RIGHT);
		totalPayAndBonus.setHorizontalAlignment(SwingConstants.RIGHT);
		totalBonusAmt.setFont(ApplicationLiterals.BOLD_FONT);
		totalPayAndBonus.setFont(ApplicationLiterals.BOLD_FONT);
		totalPayAndBonus.setForeground(Color.BLUE);
		totalBonusAmt.setForeground(Color.BLUE);
	}
}

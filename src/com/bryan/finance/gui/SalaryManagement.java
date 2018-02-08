package com.bryan.finance.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Set;

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

import com.bryan.finance.beans.Salary;
import com.bryan.finance.database.queries.QueryUtil;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.utilities.HintTextField;

public class SalaryManagement implements ActionListener {

	private JFrame frame = new JFrame("Salaries");
	private Set<Salary> salaries;
	private NumberFormat decimal = ApplicationLiterals.getNumberFormat();

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

		// Add listener action code to all input objects
		jobGrade.addActionListener(this);
		compRatio.addActionListener(this);
		STIPerf.addActionListener(this);
		MTIPerf.addActionListener(this);
	}

	private void populateJobGrades() {
		for (Integer i = 6; i < 29; i++) {
			if (!(i == 22 || (i > 23 & i < 28))) {
				jobGrade.addItem(i);
			}
		}
		jobGrade.setMaximumRowCount(10);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String comp = compRatio.getText().trim();
		String sti = STIPerf.getText().trim();
		String mti = MTIPerf.getText().trim();

		if (!(comp.equals("") || sti.equals("") || mti.equals(""))) {
			double compVal = Double.parseDouble(comp);
			double stiVal = Double.parseDouble(sti);
			double mtiVal = Double.parseDouble(mti);

			if (compVal < 80.0 || compVal > 120.0) {
				JOptionPane.showMessageDialog(frame,
						"Comp Ratio must be between 80 and 120",
						"Invalid Comp Ratio", JOptionPane.WARNING_MESSAGE);
			}

			if (stiVal < 0.0 || stiVal > 200.0) {
				JOptionPane.showMessageDialog(frame,
						"STI Performance must be between 0 and 200",
						"Invalid STI Perf", JOptionPane.WARNING_MESSAGE);
			}

			if (mtiVal < 0.0 || mtiVal > 200.0) {
				JOptionPane.showMessageDialog(frame,
						"MTI Performance must be between 0 and 200",
						"Invalid MTI Perf", JOptionPane.WARNING_MESSAGE);
			}

			Salary salary = getSelectedGradeData(Integer.parseInt(jobGrade
					.getSelectedItem().toString()));
			compVal = compVal / 100.0;
			stiVal = stiVal / 100.0;
			mtiVal = mtiVal / 100.0;

			double basePayVal = salary.getMidPay() * compVal;
			basePay.setText("$ " + decimal.format(salary.getMidPay() * compVal));

			monthlyPay.setText("$ "
					+ decimal.format((salary.getMidPay() * compVal) / 12));

			biWeeklyPay.setText("$ "
					+ decimal.format((salary.getMidPay() * compVal) / 12 / 2));

			double stiBonusPercentVal = salary.getStiTarget() * stiVal;
			STIBonusPercent.setText(decimal.format(stiBonusPercentVal) + " %");

			stiBonusPercentVal = stiBonusPercentVal / 100;
			STIBonusAmt.setText("$ "
					+ decimal.format(stiBonusPercentVal * basePayVal));

			basePayAndSTI.setText("$ "
					+ decimal.format((stiBonusPercentVal * basePayVal)
							+ basePayVal));

			if (salary.getGrade() < 8) {
				MTIBonusPercent.setText("--");
				MTIBonusAmt.setText("--");
				totalBonusAmt.setText(STIBonusAmt.getText());
				totalPayAndBonus.setText(basePayAndSTI.getText());
			} else {
				double mtiBonusPercentVal = salary.getMtiTarget() * mtiVal;
				MTIBonusPercent.setText(decimal.format(mtiBonusPercentVal)
						+ " %");

				mtiBonusPercentVal = mtiBonusPercentVal / 100;
				double mtiBonusAmt = mtiBonusPercentVal * basePayVal;
				MTIBonusAmt.setText("$ " + decimal.format(mtiBonusAmt));

				totalBonusAmt.setText("$ "
						+ decimal.format(mtiBonusAmt
								+ (stiBonusPercentVal * basePayVal)));

				double stiAndBaseAmt = (stiBonusPercentVal * basePayVal)
						+ basePayVal;
				totalPayAndBonus.setText("$ "
						+ decimal.format(mtiBonusAmt + stiAndBaseAmt));
			}

			alignOutputLabels();
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

	private void alignOutputLabels() {
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
	}
}

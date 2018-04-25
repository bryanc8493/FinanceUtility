package com.bryan.finance.gui.investments;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.bryan.finance.database.Connect;

import com.bryan.finance.database.InvestmentBalance;
import com.bryan.finance.gui.util.ApplicationControl;
import com.bryan.finance.gui.util.RequestFocusListener;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.utilities.MultiLabelButton;

public class InvestmentsTab extends JPanel {

	private static final long serialVersionUID = -3456368332519377049L;

	public final static JButton fidelity = new MultiLabelButton("Update 401K",
			MultiLabelButton.BOTTOM, Icons.FIDELITY_ICON);
	public final static JButton janus = new MultiLabelButton("Update Janus",
			MultiLabelButton.BOTTOM, Icons.JANUS_ICON);
	private Connection con;

	public InvestmentsTab() {
		con = Connect.getConnection();
		final JButton fidelityV = new MultiLabelButton("View 401K",
				MultiLabelButton.BOTTOM, Icons.FIDELITY_ICON);
		final JButton janusV = new MultiLabelButton("View Janus",
				MultiLabelButton.BOTTOM, Icons.JANUS_ICON);
		janusV.setEnabled(false);
		janus.setEnabled(false);

		JPanel investContent = new JPanel(new GridLayout(1, 4, 5, 5));
		investContent.add(fidelityV);
		investContent.add(fidelity);
		investContent.add(janusV);
		investContent.add(janus);
		investContent.setBorder(BorderFactory.createCompoundBorder(
				ApplicationLiterals.PADDED_SPACE,
				BorderFactory.createTitledBorder("Investment Actions:")));

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.add(investContent, BorderLayout.NORTH);

		this.setLayout(new BorderLayout());
		this.add(new Title("Investments"), BorderLayout.NORTH);
		this.add(wrapper, BorderLayout.CENTER);
		this.add(
				ApplicationControl.closeAndLogout(con,
						(JFrame) SwingUtilities.getRoot(this)),
				BorderLayout.SOUTH);

		fidelity.addActionListener(e -> {
			JFormattedTextField tf = new JFormattedTextField(
					ApplicationLiterals.getCurrencyFormat());
			tf.setColumns(10);
			tf.setValue(0.0);
			tf.setFont(ApplicationLiterals.APP_FONT);
			tf.addAncestorListener(new RequestFocusListener());
			int input = JOptionPane.showConfirmDialog(null, tf,
					"Updated Fidelity Balance",
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			if (input == JOptionPane.OK_OPTION) {
				String balance = tf
						.getText()
						.replace(ApplicationLiterals.DOLLAR,
								ApplicationLiterals.EMPTY)
						.replace(ApplicationLiterals.COMMA,
								ApplicationLiterals.EMPTY);

				InvestmentBalance.updateInvestmentAccount(con,
						ApplicationLiterals.FIDELITY, balance);
				JOptionPane.showMessageDialog(null,
						"Updated Fidelity Table", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});

		janus.addActionListener(e -> {
			JFormattedTextField tf = new JFormattedTextField(
					ApplicationLiterals.getCurrencyFormat());
			tf.setColumns(10);
			tf.setValue(0.0);
			tf.setFont(ApplicationLiterals.APP_FONT);
			tf.addAncestorListener(new RequestFocusListener());
			int input = JOptionPane.showConfirmDialog(null, tf,
					"Updated Janus Balance", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			if (input == JOptionPane.OK_OPTION) {
				String balance = tf.getText().replace("$", "")
						.replace(",", "");

				InvestmentBalance.updateInvestmentAccount(con,
						ApplicationLiterals.JANUS, balance);
				JOptionPane.showMessageDialog(null, "Updated Janus Table",
						"Success", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		fidelityV.addActionListener(e -> InvestmentBalance.getLatestFidelityBalance(con));

		janusV.addActionListener(e -> InvestmentBalance.getLatestJanusBalance(con));
	}
}

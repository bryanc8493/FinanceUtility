package com.bryan.finance.gui.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;

import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.VerifyAccess;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.program.FinanceUtility;

public class ApplicationControl {

	private static final Logger logger = Logger
			.getLogger(ApplicationControl.class);

	public static JPanel closeAndLogout(final Connection con, final JFrame frame) {
		final JButton close = new PrimaryButton("Close", Icons.EXIT_ICON);
		final JButton logOut = new PrimaryButton("Log off", Icons.LOGOFF_ICON);

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(logOut, BorderLayout.WEST);
		panel.add(Box.createHorizontalGlue());
		panel.add(close, BorderLayout.EAST);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

		final JPanel panelWrapper = new JPanel(new BorderLayout());
		panelWrapper.add(new JSeparator(JSeparator.HORIZONTAL),
				BorderLayout.NORTH);
		panelWrapper.add(panel, BorderLayout.SOUTH);

		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to exit?", "Confirm",
						JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
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
			}
		});

		logOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to log out?", "Confirm",
						JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					if (con != null) {
						try {
							con.close();
						} catch (SQLException e1) {
							throw new AppException(e1);
						}
					}
					frame.dispose();
					logger.info("Closed by user");

					VerifyAccess.CheckAccess();
				}
			}
		});
		return panelWrapper;
	}
}

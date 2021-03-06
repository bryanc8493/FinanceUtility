package com.bryan.finance.gui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;

import com.bryan.finance.database.queries.Transactions;
import com.bryan.finance.gui.finance.Savings;
import com.bryan.finance.utilities.DateUtil;
import org.apache.log4j.Logger;

import com.alee.laf.WebLookAndFeel;
import com.bryan.finance.database.Connect;
import com.bryan.finance.gui.account.UserManagement;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;

public class MenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(MenuBar.class);

	private JMenuItem userMgmt = new JMenuItem("Lock/Unlock Users",
			KeyEvent.VK_L);
	private JMenuItem salary = new JMenuItem("Salary", KeyEvent.VK_A);
	private JMenuItem modifyAppSettings = new JMenuItem("Modify");
	private JMenuItem modifyDBSettings = new JMenuItem("Modify");

	private JMenuBar menuBar = new JMenuBar();

	JMenuBar getMenu() {
		return menuBar;
	}

	MenuBar(final JFrame frame, final JTabbedPane menuTabs) {
		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic(KeyEvent.VK_T);

		JMenu themeMenu = new JMenu("Theme");
		themeMenu.setMnemonic(KeyEvent.VK_H);

		JMenu settingsMenu = new JMenu("Settings");
		settingsMenu.setMnemonic(KeyEvent.VK_S);

		String releaseDate = DateUtil.getDeploymentDate();
		JMenu versionAndDate = new JMenu();
		if (ApplicationLiterals.isFromWorkspace()) {
			versionAndDate.setIcon(Icons.RED_DOT);
			versionAndDate.setText("v" + ApplicationLiterals.VERSION + "  (Development)");
		} else {
			versionAndDate.setIcon(Icons.GREEN_DOT);
			versionAndDate.setText("v" + ApplicationLiterals.VERSION + "  (Released: " + releaseDate + ")");
		}
		versionAndDate.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		JMenuItem backup = new JMenuItem("Backup Databases", KeyEvent.VK_B);
		backup.setIcon(Icons.BACKUP_ICON);
		JMenuItem monthReports = new JMenuItem("Monthly Summaries",
				KeyEvent.VK_M);
		monthReports.setIcon(Icons.SUMMARY_ICON);
		JMenuItem savings = new JMenuItem("View Savings", KeyEvent.VK_S);
		savings.setIcon(Icons.SAVINGS_ICON);
		salary.setIcon(Icons.SALARY_ICON);
		userMgmt.setIcon(Icons.USER_MGMT_ICON);
		JMenuItem changePass = new JMenuItem("Change Password", KeyEvent.VK_C);
		changePass.setIcon(Icons.CHANGE_PASS_ICON);
		JMenuItem refresh = new JMenuItem("Refresh");
		refresh.setIcon(Icons.REFRESH_ICON);
		toolsMenu.add(backup);
		toolsMenu.addSeparator();
		toolsMenu.add(changePass);
		toolsMenu.add(userMgmt);
		toolsMenu.addSeparator();
		toolsMenu.add(monthReports);
		toolsMenu.add(salary);
		toolsMenu.add(savings);
		toolsMenu.addSeparator();
		toolsMenu.add(refresh);

		JMenuItem nimbusTheme = new JMenuItem("Nimbus", KeyEvent.VK_N);
		nimbusTheme.setIcon(Icons.NIMBUS_ICON);
		JMenuItem metalTheme = new JMenuItem("Metal", KeyEvent.VK_M);
		JMenuItem windowsTheme = new JMenuItem("Windows", KeyEvent.VK_W);
		windowsTheme.setIcon(Icons.WINDOWS_ICON);
		JMenuItem windowsClassicTheme = new JMenuItem("Windows Classic",
				KeyEvent.VK_I);
		windowsClassicTheme.setIcon(Icons.WINDOWS_CLASSIC_ICON);
		JMenuItem webTheme = new JMenuItem("Web", KeyEvent.VK_E);
		JMenuItem motifTheme = new JMenuItem("Motif", KeyEvent.VK_O);
		motifTheme.setIcon(Icons.MOTIF_ICON);
		themeMenu.add(metalTheme);
		themeMenu.add(motifTheme);
		themeMenu.add(nimbusTheme);
		themeMenu.add(webTheme);
		themeMenu.add(windowsTheme);
		themeMenu.add(windowsClassicTheme);

		JMenu databaseSettings = new JMenu("Database");
		JMenuItem viewDBSettings = new JMenuItem("View");
		databaseSettings.add(viewDBSettings);
		databaseSettings.add(modifyDBSettings);

		JMenu appSettings = new JMenu("Application");
		JMenuItem viewAppSettings = new JMenuItem("View");
		appSettings.add(viewAppSettings);
		appSettings.add(modifyAppSettings);

		settingsMenu.add(appSettings);
		settingsMenu.addSeparator();
		settingsMenu.add(databaseSettings);

		setPermissions(Connect.getUsersPermission());

		menuBar.add(toolsMenu);
		menuBar.add(themeMenu);
		menuBar.add(settingsMenu);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(versionAndDate);

		backup.addActionListener((e) -> new Backup());

		refresh.addActionListener(e -> {
			int currentTabIndex = menuTabs.getSelectedIndex();
			frame.dispose();
			MainMenu.modeSelection(false, currentTabIndex);
		});

		changePass.addActionListener(e -> {
			if (!Connect.getCurrentUser().equalsIgnoreCase("root")) {
				UserManagement.changePassword(false,
						Connect.getCurrentUser());
			} else {
				JOptionPane.showMessageDialog(frame,
						"Password cannot be changed for root user",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		userMgmt.addActionListener((e) -> new UserManagement());

		salary.addActionListener((e) -> new SalaryManagement());

		webTheme.addActionListener(e -> {
			try {
				UIManager.setLookAndFeel(WebLookAndFeel.class
						.getCanonicalName());
				logger.debug("Previewing Web L&F");
			} catch (Exception ex) {
				logger.error(ex.toString());
			}
			frame.dispose();
			MainMenu.modeSelection(true, 0);
		});

		nimbusTheme.addActionListener(e -> {
			try {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
				logger.debug("Previewing Nimbus L&F");
			} catch (Exception ex) {
				logger.error(ex.toString());
			}
			frame.dispose();
			MainMenu.modeSelection(true, 0);
		});

		windowsTheme.addActionListener(e -> {
			try {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				logger.debug("Previewing windows L&F");
			} catch (Exception ex) {
				logger.error(ex.toString());
			}
			frame.dispose();
			MainMenu.modeSelection(true, 0);
		});

		windowsClassicTheme.addActionListener(e -> {
			try {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
				logger.debug("Previewing windows classic L&F");
			} catch (Exception ex) {
				logger.error(ex.toString());
			}
			frame.dispose();
			MainMenu.modeSelection(true, 0);
		});

		motifTheme.addActionListener(e -> {
			try {
				UIManager
						.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
				logger.debug("Previewing motif L&F");
			} catch (Exception ex) {
				logger.error(ex.toString());
			}
			frame.dispose();
			MainMenu.modeSelection(true, 0);
		});

		metalTheme.addActionListener(e -> {
			try {
				UIManager
						.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
				logger.debug("Previewing metal L&F");
			} catch (Exception ex) {
				logger.error(ex.toString());
			}
			frame.dispose();
			MainMenu.modeSelection(true, 0);
		});

		monthReports.addActionListener(e -> {
			JFrame f = new JFrame("Monthly Summary Data");
			JPanel p = new JPanel(new BorderLayout(10, 0));
			JLabel label = new Title(
					"Monthly Summary Data (Since January 2016)");
			p.add(label, BorderLayout.NORTH);
			p.add(getMonthReportsData(), BorderLayout.SOUTH);
			p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			f.add(p);
			f.setIconImage(Icons.APP_ICON.getImage());
			f.pack();
			f.setVisible(true);
			f.setLocationRelativeTo(null);
		});

		savings.addActionListener((e) -> new Savings());

		viewAppSettings.addActionListener(e -> {
			logger.debug("Displaying app settings");
			new AppSettings(false);
		});

		modifyAppSettings.addActionListener(e -> {
			logger.debug("Modifying app settings");
			new AppSettings(true);
		});

		viewDBSettings.addActionListener(e -> {
			logger.debug("Dislaying database settings");
			new DatabaseSettings(false);
		});

		modifyDBSettings.addActionListener(e -> {
			logger.debug("Modifying database settings");
			new DatabaseSettings(true);
		});
	}

	private void setPermissions(char permission) {
		if (permission == ApplicationLiterals.VIEW_ONLY) {
			userMgmt.setEnabled(false);
			salary.setEnabled(false);
			modifyAppSettings.setEnabled(false);
			modifyDBSettings.setEnabled(false);
		}
	}

	private static JScrollPane getMonthReportsData() {
		Object[][] records = Transactions.getMonthlySummaries();
		Object[] columnNames = { "MONTH", "YEAR", "TOTAL EXPENSES",
				"TOTAL INCOME", "CASH FLOW" };
		JTable table = new JTable(records, columnNames);
		final JScrollPane sp = new JScrollPane(table);
		sp.setViewportView(table);
		sp.setVisible(true);
		Dimension d = table.getPreferredSize();
		sp.setPreferredSize(new Dimension(d.width + 250,
				table.getRowHeight() * 15));
		return sp;
	}
}

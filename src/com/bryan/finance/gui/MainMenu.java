package com.bryan.finance.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;

import com.bryan.finance.beans.Transaction;
import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.database.Connect;
import com.bryan.finance.database.queries.Queries;
import com.bryan.finance.database.queries.Balance;
import com.bryan.finance.database.queries.Transactions;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.account.AccountsTab;
import com.bryan.finance.gui.address.AddressTab;
import com.bryan.finance.gui.finance.InsertRecord;
import com.bryan.finance.gui.finance.ModifyRecords;
import com.bryan.finance.gui.finance.TransactionRecord;
import com.bryan.finance.gui.investments.InvestmentsTab;
import com.bryan.finance.gui.util.ApplicationControl;
import com.bryan.finance.gui.util.CustomReport;
import com.bryan.finance.gui.util.Loading;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.utilities.MultiLabelButton;

public class MainMenu extends Icons {

	public static final ImageIcon appIcon = APP_ICON;
	private static Logger logger = Logger.getLogger(MainMenu.class);
	private static NumberFormat decimal = ApplicationLiterals.getNumberFormat();

	private static boolean isFutureBalancePositive = false;

	public static void modeSelection(final boolean showThemeButton,
			int persistedTab) {
		logger.debug("Initializing and generating main menu GUI...");
		final JFrame frame = new JFrame("Finance Utility");

		String viewingAmount = ReadConfig
				.getConfigValue(ApplicationLiterals.VIEWING_AMOUNT_MAX);

		final JButton insert = new MultiLabelButton("New",
				MultiLabelButton.BOTTOM, INSERT_ICON);
		final JButton modify = new MultiLabelButton("Modify",
				MultiLabelButton.BOTTOM, DELETE_ICON);
		final JButton report = new MultiLabelButton("Reporting",
				MultiLabelButton.BOTTOM, REPORT_ICON);
		final JButton lastRecords = new MultiLabelButton("Last "
				+ viewingAmount, MultiLabelButton.BOTTOM, QUERY_ICON);

		// Get current balance as of current date and time
		final Connection con = Connect.getConnection();
		String amount = Balance.getTodaysBalance();
		amount = decimal.format(Double.parseDouble(amount));

		// Get full balance (todays balance minus future payments excluding
		// unpaid credits)
		String futureBalance = Balance.getFutureBalance();
		futureBalance = decimal.format(Double.parseDouble(futureBalance));

		// Get actual balance
		String trueBalance = Balance.getTrueBalance();
		trueBalance = decimal.format(Double.parseDouble(trueBalance));

		// Get data for last specified (in config) past entries and put in
		// scroll pane for table
		Object[][] records = Transactions.getPastEntries();
		Object[] columnNames = { "ID", "TITLE", "TYPE", "DATE", "AMOUNT" };
		final JTable table = new JTable(records, columnNames);
		final JScrollPane entriesScrollPane = new JScrollPane(table);
		entriesScrollPane.setViewportView(table);
		entriesScrollPane.setVisible(true);
		Dimension d = table.getPreferredSize();
		entriesScrollPane.setPreferredSize(new Dimension(d.width * 2, table
				.getRowHeight() * 15));

		String futurePayments = Balance.getFuturePayments();
		final JButton futureBalBtn = new JButton();
		if (futurePayments == null) {
			futureBalBtn.setText(ApplicationLiterals.EMPTY);
			futureBalBtn.setVisible(false);
		} else {
			Double amt = Double.parseDouble(futurePayments);
			futurePayments = decimal.format(amt);
			isFutureBalancePositive = amt > 0.00;
			futureBalBtn.setText("<html><u>($ " + futurePayments
					+ ")</u></html>");
		}

		futureBalBtn.setBorderPainted(false);
		futureBalBtn.setContentAreaFilled(false);
		futureBalBtn.setFocusPainted(false);
		futureBalBtn.setHorizontalAlignment(SwingConstants.LEFT);
		futureBalBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		if (isFutureBalancePositive) {
			futureBalBtn.setForeground(Color.GREEN);
		} else {
			futureBalBtn.setForeground(Color.RED);
		}

		String credits = Balance.getCreditBalance();
		final JButton creditBalBtn = new JButton();
		if (credits == null) {
			creditBalBtn.setText(ApplicationLiterals.EMPTY);
			creditBalBtn.setVisible(false);
		} else {
			credits = decimal.format(Double.parseDouble(credits));
			creditBalBtn.setText("<html><u>($ " + credits + ")</u></html>");
		}

		creditBalBtn.setBorderPainted(false);
		creditBalBtn.setContentAreaFilled(false);
		creditBalBtn.setFocusPainted(false);
		creditBalBtn.setHorizontalAlignment(SwingConstants.LEFT);
		creditBalBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		creditBalBtn.setForeground(Color.RED);

		JPanel grid = new JPanel(new GridLayout(3, 3));
		grid.add(new JLabel("Available Balance:"));
		grid.add(new JLabel("$ " + amount));
		grid.add(new JLabel());
		grid.add(new JLabel("Future Balance:"));
		grid.add(new JLabel("$ " + futureBalance));
		grid.add(futureBalBtn);
		grid.add(new JLabel("Balance (- Credits):"));
		grid.add(new JLabel("$ " + trueBalance));
		grid.add(creditBalBtn);
		grid.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 20));

		JPanel top = new JPanel(new BorderLayout());
		top.add(grid, BorderLayout.NORTH);
		top.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.SOUTH);
		top.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

		JPanel dbBtnContent = new JPanel(new GridLayout(1, 4, 5, 5));
		dbBtnContent.add(insert);
		dbBtnContent.add(modify);
		dbBtnContent.add(report);
		dbBtnContent.add(lastRecords);
		dbBtnContent.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel content = new JPanel(new BorderLayout(0, 8));
		content.add(dbBtnContent, BorderLayout.NORTH);

		Border space = BorderFactory.createEmptyBorder(10, 25, 15, 25);
		content.setBorder(space);

		JPanel center = new JPanel(new BorderLayout());
		center.add(content, BorderLayout.NORTH);
		content.add(getLatestRecords(), BorderLayout.SOUTH);

		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
		final JButton setThemeDefault = new JButton(
				"Make This My Default Theme");
		setThemeDefault.setAlignmentX(JButton.CENTER_ALIGNMENT);
		if (!showThemeButton) {
			setThemeDefault.setVisible(false);
		}
		bottom.add(setThemeDefault);
		bottom.add(ApplicationControl.closeAndLogout(con, frame));

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(top, BorderLayout.NORTH);
		mainPanel.add(center, BorderLayout.CENTER);
		mainPanel.add(bottom, BorderLayout.SOUTH);

		JTabbedPane screen = new JTabbedPane(JTabbedPane.TOP);
		screen.add("Finance", mainPanel);
		if (Connect.getUsersPermission() == '1') {
			screen.add("Investments", new InvestmentsTab());
			screen.add("Accounts", new AccountsTab());
		}
		screen.add("Addresses", new AddressTab());
		screen.setSelectedIndex(persistedTab);

		MenuBar m = new MenuBar(frame, screen);
		JMenuBar b = m.getMenu();

		frame.add(screen);
		frame.setIconImage(appIcon.getImage());
		frame.setJMenuBar(b);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JRootPane rp = SwingUtilities.getRootPane(insert);
		rp.setDefaultButton(insert);
		frame.pack();
		Loading.terminate();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		// If user permission is read only (0) then disable all GUI elements for
		// possible editing and deleting
		if (Connect.getUsersPermission() == '0') {
			logger.warn("Read Permission only - Editing and deleting disabled");
			modify.setEnabled(false);
			InvestmentsTab.fidelity.setEnabled(false);
			InvestmentsTab.janus.setEnabled(false);
		}

		insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				InsertRecord.InsertFrame();
			}
		});

		modify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				ModifyRecords.editData();
			}
		});

		report.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				CustomReport.selectReport();
			}
		});

		lastRecords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String viewingAmount = ReadConfig
						.getConfigValue(ApplicationLiterals.VIEWING_AMOUNT_MAX);
				logger.debug("Displaying last " + viewingAmount + " records");
				JFrame f = new JFrame("Past " + viewingAmount + " Records");
				JPanel p = new JPanel(new BorderLayout(10, 0));
				JLabel label = new Title("Last " + viewingAmount
						+ " Transactions");
				p.add(label, BorderLayout.NORTH);
				p.add(entriesScrollPane, BorderLayout.SOUTH);
				f.add(p);
				f.pack();
				f.setVisible(true);
				f.setLocationRelativeTo(null);
			}
		});

		setThemeDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				makeCurrentThemeDefault();
				setThemeDefault.setVisible(false);
			}
		});

		futureBalBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("Displaying future transaction records");
				JFrame f = new JFrame("Future Transactions");
				JPanel p = new JPanel(new BorderLayout(10, 0));
				JLabel label = new Title("Future Transactions");
				p.add(label, BorderLayout.NORTH);
				p.add(getFutureRecordsPane(), BorderLayout.SOUTH);
				f.add(p);
				f.pack();
				f.setVisible(true);
				f.setLocationRelativeTo(null);
			}
		});

		creditBalBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.debug("Displaying unpaid credit records");
				JFrame f = new JFrame("Unpaid Credits");
				JPanel p = new JPanel(new BorderLayout(10, 0));
				JLabel label = new Title("Unpaid Credit Card Transactions");
				p.add(label, BorderLayout.NORTH);
				p.add(getCreditRecordsPane(), BorderLayout.SOUTH);
				f.add(p);
				f.pack();
				f.setVisible(true);
				f.setLocationRelativeTo(null);
			}
		});
	}

	private static void makeCurrentThemeDefault() {
		File themeDir = new File(ApplicationLiterals.THEME_DIR);
		String themeFileName = ReadConfig
				.getConfigValue(ApplicationLiterals.THEME_FILE_NAME);
		themeDir.mkdirs();
		File themeFile = new File(themeDir + ApplicationLiterals.SLASH
				+ themeFileName);
		String currentTheme = UIManager.getLookAndFeel().toString();
		currentTheme = currentTheme.substring(1, currentTheme.length() - 1);
		String[] split = currentTheme.split(ApplicationLiterals.DASH);
		String themeClass = split[split.length - 1].trim();
		logger.info("Set new default L&F to " + themeClass + " theme");

		try {
			FileWriter fw = new FileWriter(themeFile);
			fw.write(themeClass);
			fw.close();
		} catch (IOException e) {
			throw new AppException(e);
		}
	}

	private static JScrollPane getFutureRecordsPane() {
		Object[] columns = { "Title", "Type", "Category", "Transaction_Date", "Amount" };

		JTable table = new JTable(Transactions.getFutureRecords(), columns);
		JScrollPane sp = new JScrollPane(table);
		sp.setViewportView(table);
		sp.setVisible(true);
		Dimension d = table.getPreferredSize();
		sp.setPreferredSize(new Dimension((d.width * 2) - 150, table
				.getRowHeight() * 10));

		return sp;
	}

	private static JScrollPane getCreditRecordsPane() {
		Object[] columns = { "Title", "Category", "Date", "Amount" };

		JTable table = new JTable(Transactions.getUnpaidCreditRecords(), columns);
		JScrollPane sp = new JScrollPane(table);
		sp.setViewportView(table);
		sp.setVisible(true);
		Dimension d = table.getPreferredSize();
		sp.setPreferredSize(new Dimension(d.width * 2,
				table.getRowHeight() * 10));

		return sp;
}

	private static JScrollPane getLatestRecords() {
		Object[][] records = Transactions.getPastEntries();
		Object[][] partialRecords = getPartialDataColumns(records);
		Object[] columnNames = { "Transactions ID", "Title", "Type", "Amount" };

		DefaultTableModel model = new DefaultTableModel(partialRecords,
				columnNames) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		final JTable table = new JTable(model);
		final JScrollPane addrSP = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		addrSP.setViewportView(table);
		addrSP.setVisible(true);
		Dimension d = table.getPreferredSize();
		addrSP.setPreferredSize(new Dimension(d.width,
				table.getRowHeight() * 12));

		TableColumn tranIdColumn = table.getColumnModel().getColumn(0);
		table.getColumnModel().removeColumn(tranIdColumn);

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String id = table.getModel().getValueAt(table.getSelectedRow(),0).toString();

					Transaction selectedTran = Transactions.getSpecifiedTransaction(id);
					new TransactionRecord(selectedTran);
				}
			}
		});

		return addrSP;
	}

	private static Object[][] getPartialDataColumns(Object[][] data) {

		Object[][] returnData = new Object[data.length][4];
		for (int i = 0; i < data.length; i++) {
			returnData[i][0] = data[i][0];
			returnData[i][1] = data[i][1];
			returnData[i][2] = data[i][2];
			returnData[i][3] = data[i][4];
		}
		return returnData;
	}
}

package com.bryan.finance.gui.finance;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.bryan.finance.database.queries.Transactions;
import org.apache.log4j.Logger;

import com.bryan.finance.beans.Transaction;
import com.bryan.finance.beans.UpdatedRecord;
import com.bryan.finance.database.Updates;
import com.bryan.finance.gui.MainMenu;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.Icons;

public class ModifyRecords {

	private static JTable table;
	private static List<UpdatedRecord> updates;
	private static Logger logger = Logger.getLogger(ModifyRecords.class);
	private static final JButton updateBtn = new PrimaryButton("Update");


	public static void editData() {
		logger.debug("Displaying GUI for user to modify transactions");
		final JFrame frame = new JFrame("Edit/Delete Transactions");
		ImageIcon appIcon = Icons.APP_ICON;

		JLabel title = new Title("Modify Transactions");

		final JButton deleteBtn = new PrimaryButton("Delete");
		final JButton cancelBtn = new PrimaryButton("Cancel");
		updateBtn.setEnabled(false);

		final JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottom.add(cancelBtn);
		bottom.add(deleteBtn);
		bottom.add(updateBtn);
		bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(title, BorderLayout.NORTH);
		mainPanel.add(getTransactionData(), BorderLayout.CENTER);
		mainPanel.add(bottom, BorderLayout.SOUTH);

		frame.add(mainPanel);
		frame.setIconImage(appIcon.getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		updates = new ArrayList<>();

		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				MainMenu.modeSelection(false, 0);
			}
		});

		updateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Updates.changeTransactions(updates);
			}
		});

		deleteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Call DB method to update all changed records
				int row = table.getSelectedRow();
				if (row != -1) {
					Transaction tran = new Transaction();
					tran.setTransactionID((String) table.getValueAt(row, 0));
					tran.setTitle((String) table.getValueAt(row, 1));
					tran.setType((String) table.getValueAt(row, 2));
					tran.setDate((String) table.getValueAt(row, 3));
					tran.setAmount((String) table.getValueAt(row, 4));
					int choice = JOptionPane
							.showConfirmDialog(
									null,
									"Are you sure you want to delete the selected record?",
									"Confirm", JOptionPane.YES_NO_OPTION);
					if (choice == JOptionPane.YES_OPTION) {
						frame.dispose();
						Updates.deleteTransaction(tran);
						editData();
//						MainMenu.modeSelection(false, 0);
					}
				} else {
					JOptionPane.showMessageDialog(null,
							"Please select a record to delete", "No Selection",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
	}

	private static JScrollPane getTransactionData() {
		Object[][] records = Transactions.getPastEntries();
		Object[] columnNames = { "ID", "Title", "Type", "Date", "Amount" };

		// Creating table model to hold data and only make certain columns
		// editable
		DefaultTableModel model = new DefaultTableModel(records, columnNames) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column == 0 || column == 2) {
					return false;
				} else {
					return true;
				}
			}
		};
		table = new JTable(model);

		// Put table in Scroll Pane
		final JScrollPane sp = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setViewportView(table);
		sp.setVisible(true);
		Dimension d = table.getPreferredSize();
		sp.setPreferredSize(new Dimension(d.width * 2,
				table.getRowHeight() * 15));
		final Map<Integer, String> map = getAttributeMap();

		// Add action listener on the table model to catch and time a cell is
		// updated and
		// temporarily store the cells data that was changed and the ID of that
		// record to update in database
		table.getModel().addTableModelListener(new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				String changedData;
				String ID;
				int row = table.getSelectedRow();
				int column = table.getSelectedColumn();
				changedData = (String) table.getValueAt(row, column);
				ID = (String) table.getValueAt(row, 0);
				UpdatedRecord changedRecord = new UpdatedRecord();
				changedRecord.setID(ID);
				changedRecord.setAttribute(map.get(column));
				changedRecord.setData(changedData);
				updates.add(changedRecord);
				updateBtn.setEnabled(true);
			}
		});
		return sp;
	}

	private static Map<Integer, String> getAttributeMap() {

		Map<Integer, String> map = new HashMap<>();
		map.put(1, "TITLE");
		map.put(3, "TRANSACTION_DATE");
		map.put(4, "AMOUNT");
		return map;
	}
}

package com.bryan.finance.gui.address;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.bryan.finance.database.queries.Addresses;
import com.bryan.finance.gui.MainMenu;
import org.apache.log4j.Logger;

import com.bryan.finance.beans.Address;
import com.bryan.finance.beans.UpdatedRecord;
import com.bryan.finance.database.Connect;
import com.bryan.finance.database.Updates;
import com.bryan.finance.gui.util.ApplicationControl;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.utilities.MultiLabelButton;

public class AddressTab extends JPanel {

	private static final long serialVersionUID = -6032099937844408280L;
	private static List<UpdatedRecord> updates;
	private static JTable table;
	private static Logger logger = Logger.getLogger(AddressTab.class);

	public AddressTab() {
		Connection con = Connect.getConnection();
		logger.debug("Initializing and populating Address Tab");
		final JButton view = new MultiLabelButton("View Addresses",
				MultiLabelButton.BOTTOM, Icons.VIEW_ICON);
		final JButton add = new MultiLabelButton(" New Address ",
				MultiLabelButton.BOTTOM, Icons.ADD_ICON);
		final JButton edit = new MultiLabelButton(" Edit Addresses ",
				MultiLabelButton.BOTTOM, Icons.EDIT_ICON);

		if (Connect.getUsersPermission() == ApplicationLiterals.VIEW_ONLY) {
			logger.warn("Read permission only, edit button will be disabled");
			edit.setEnabled(false);
		}

		JLabel title = new Title("All Active Addresses");

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttons.add(view);
		buttons.add(add);
		buttons.add(edit);

		JPanel content = new JPanel(new BorderLayout());
		content.add(buttons, BorderLayout.NORTH);
		content.add(getPartialAddressData(), BorderLayout.SOUTH);

		content.setBorder(ApplicationLiterals.PADDED_SPACE);

		this.setLayout(new BorderLayout(10, 10));
		this.add(title, BorderLayout.NORTH);
		this.add(content, BorderLayout.CENTER);
		this.add(
				ApplicationControl.closeAndLogout(con,
						(JFrame) SwingUtilities.getRoot(this)),
				BorderLayout.SOUTH);

		view.addActionListener(e ->  {
			JFrame f = new JFrame("All Addresses");
			JPanel p = new JPanel(new BorderLayout(10, 0));
			JLabel label = new Title("All Addresses");
			label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			p.add(label, BorderLayout.NORTH);
			p.add(getAddressData(), BorderLayout.SOUTH);
			f.add(p);
			f.pack();
			f.setVisible(true);
			f.setLocationRelativeTo(null);
		});

		add.addActionListener(e -> InsertAddress.addNewAddress());

		edit.addActionListener(e ->  {
			// Put data in frame
			logger.debug("User Editing Address Data");
			final JFrame f = new JFrame("Edit Addresses");
			JPanel p = new JPanel(new BorderLayout(10, 0));
			JLabel label = new Title("Edit Addresses");
			JButton update = new JButton("Update");
			JButton delete = new JButton("Delete");
			delete.setCursor(new Cursor(Cursor.HAND_CURSOR));
			update.setCursor(new Cursor(Cursor.HAND_CURSOR));
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			buttonPanel.add(delete);
			buttonPanel.add(update);
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
			p.add(label, BorderLayout.NORTH);
			p.add(getAddressData(), BorderLayout.CENTER);
			p.add(buttonPanel, BorderLayout.SOUTH);
			f.add(p);
			f.pack();
			f.setVisible(true);
			f.setLocationRelativeTo(null);

			updates = new ArrayList<>();

			update.addActionListener(ex -> {
				f.dispose();
				Updates.changeAddresses(updates);
				MainMenu.closeWindow();
				JOptionPane.showMessageDialog(null,
						"Successfully updated addresses",
						"Updated!",
						JOptionPane.INFORMATION_MESSAGE);
				MainMenu.modeSelection(false,3);
			});

			delete.addActionListener(exc ->  {
				int row = table.getSelectedRow();
				if (row != -1) {
					String ID = (String) table.getValueAt(row, 0);
					int choice = JOptionPane
							.showConfirmDialog(
									null,
									"Are you sure you want to delete the selected record?",
									"Confirm",
									JOptionPane.YES_NO_OPTION);
					if (choice == JOptionPane.YES_OPTION) {
						f.dispose();
						Updates.deleteAddress(ID);
						MainMenu.closeWindow();
						JOptionPane.showMessageDialog(null,
								"Record deleted successfully",
								"Deleted!",
								JOptionPane.INFORMATION_MESSAGE);
						MainMenu.modeSelection(false,3);
					}
				} else {
					JOptionPane
							.showMessageDialog(null,
									"Please select a record to delete",
									"No Selection",
									JOptionPane.WARNING_MESSAGE);
				}
			});
		});
	}

	private JScrollPane getAddressData() {
		Object[][] records = Addresses.getAddresses();
		Object[] columnNames = { "ID", "Last Name", "First Name(s)", "Address",
				"City", "State", "ZIP" };

		// Creating table model to hold data and only make certain columns
		// editable
		DefaultTableModel model = new DefaultTableModel(records, columnNames) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return (column != 0);
			}
		};
		table = new JTable(model);

		JScrollPane addrSP = new JScrollPane(table);
		addrSP.setViewportView(table);
		addrSP.setVisible(true);
		Dimension d = table.getPreferredSize();
		addrSP.setPreferredSize(new Dimension(d.width * 2,
				table.getRowHeight() * 18));
		final Map<Integer, String> map = getAttributeMap();

		table.getModel().addTableModelListener(e -> {
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
		});
		return addrSP;
	}

	private JScrollPane getPartialAddressData() {
		Object[][] records = Addresses.getAddresses();
		Object[][] partialRecords = getFirstTwoDataColumns(records);
		Object[] columnNames = { "Last Name", "First Name(s)" };

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

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = table.getSelectedRow();
					int column = table.getSelectedColumn();
					int columnOpposite = 0;
					boolean isLastName = false;
					if (column == 0) {
						isLastName = true;
						columnOpposite = 1;
					}
					String cell = (String) table.getValueAt(row, column);
					String otherCell = (String) table.getValueAt(row,
							columnOpposite);

					String firstNameValue;
					String lastNameValue;
					if (isLastName) {
						lastNameValue = cell;
						firstNameValue = otherCell;
					} else {
						lastNameValue = otherCell;
						firstNameValue = cell;
					}

					Address address = Addresses.getSpecifiedAddress(
							lastNameValue, firstNameValue);
					new AddressRecord(address);
				}
			}
		});
		return addrSP;
	}

	private Object[][] getFirstTwoDataColumns(Object[][] data) {

		Object[][] returnData = new Object[data.length][2];
		for (int i = 0; i < data.length; i++) {
			returnData[i][0] = data[i][1];
			returnData[i][1] = data[i][2];
		}
		return returnData;
	}

	private Map<Integer, String> getAttributeMap() {

		Map<Integer, String> map = new HashMap<>();
		map.put(1, "LAST_NAME");
		map.put(2, "FIRST_NAMES");
		map.put(3, "ADDRESS");
		map.put(4, "CITY");
		map.put(5, "STATE");
		map.put(6, "ZIP");
		return map;
	}
}

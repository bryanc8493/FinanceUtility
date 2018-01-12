package com.bryan.finance.gui.address;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.bryan.finance.beans.Address;
import com.bryan.finance.database.Queries;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;

public class InsertAddress {

	private static Logger logger = Logger.getLogger(InsertAddress.class);

	public static void InsertFrame(final Connection con) throws ParseException {
		logger.debug("Displaying GUI to insert new Address");
		final JFrame frame = new JFrame("New Address");

		final JLabel LnameLabel = new JLabel("* Last Name");
		final JTextField LnameField = new JTextField();

		final JLabel FnameLabel = new JLabel(
				"* First Name(s)                             ");
		final JTextField FnameField = new JTextField();

		final JLabel addressLabel = new JLabel("* Address");
		final JTextField addressField = new JTextField();

		final JLabel cityLabel = new JLabel("City");
		final JTextField cityField = new JTextField();

		final JLabel stateLabel = new JLabel("State");
		final JComboBox<String> states = new JComboBox<String>(
				ApplicationLiterals.STATE_CODES);
		states.setMaximumRowCount(12);

		final JLabel zipLabel = new JLabel("Zip Code");
		final JTextField zipField = new JTextField();

		final JButton insert = new PrimaryButton("    Insert    ");
		final JButton close = new PrimaryButton("    Close    ");

		final JLabel missingField = new JLabel();
		missingField.setForeground(Color.RED);
		missingField.setVisible(false);

		JPanel grid = new JPanel();
		grid.setLayout(new GridLayout(6, 2, 5, 10));
		grid.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
		grid.add(LnameLabel);
		grid.add(LnameField);
		grid.add(FnameLabel);
		grid.add(FnameField);
		grid.add(addressLabel);
		grid.add(addressField);
		grid.add(cityLabel);
		grid.add(cityField);
		grid.add(stateLabel);
		grid.add(states);
		grid.add(zipLabel);
		grid.add(zipField);

		JPanel missing = new JPanel();
		missing.setLayout(new FlowLayout(FlowLayout.CENTER));
		missing.add(missingField);

		JPanel middle = new JPanel();
		middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
		middle.add(grid);
		middle.add(missing);

		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttons.add(close);
		buttons.add(insert);

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		JLabel frameTitle = new Title("Add New Address");
		main.add(frameTitle, BorderLayout.NORTH);
		main.add(middle, BorderLayout.CENTER);
		main.add(buttons, BorderLayout.SOUTH);

		frame.add(main);
		frame.setIconImage(Icons.APP_ICON.getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		JRootPane rp = SwingUtilities.getRootPane(insert);
		rp.setDefaultButton(insert);
		rp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});

		insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (LnameField.getText().trim()
						.equals(ApplicationLiterals.EMPTY)) {
					missingField.setText("Last name cannot be blank");
					missingField.setVisible(true);
					frame.pack();
				}

				else if (FnameField.getText().trim()
						.equals(ApplicationLiterals.EMPTY)) {
					missingField.setText("First name cannot be blank");
					missingField.setVisible(true);
					frame.pack();
				}

				else if (addressField.getText().trim()
						.equals(ApplicationLiterals.EMPTY)) {
					missingField.setText("Address cannot be blank");
					missingField.setVisible(true);
					frame.pack();
				}

				else {
					Address address = new Address();
					address.setLastName(LnameField.getText().trim());
					address.setFirstName(FnameField.getText().trim());
					address.setAddress(addressField.getText().trim());
					address.setCity(cityField.getText().trim());
					address.setState(states.getSelectedItem().toString());
					address.setZipcode(zipField.getText().trim());

					int recordCount = Queries.newAddress(address);
					if (recordCount != 1) {
						missingField
								.setText("Error inserting new address - check database");
						logger.error("Error inserting new address - check database");
						missingField.setVisible(true);
						frame.pack();
					} else {
						frame.dispose();
						JOptionPane.showMessageDialog(null,
								"New Address added successfully!", "Success",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
	}
}

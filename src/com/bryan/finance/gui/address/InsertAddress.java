package com.bryan.finance.gui.address;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

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

import com.bryan.finance.database.queries.Addresses;
import com.bryan.finance.gui.MainMenu;
import com.bryan.finance.utilities.HintTextField;
import org.apache.log4j.Logger;

import com.bryan.finance.beans.Address;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.PromptComboBoxRenderer;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;

public class InsertAddress {

	private static Logger logger = Logger.getLogger(InsertAddress.class);

	public static void addNewAddress() {
		logger.debug("Displaying GUI to insert new Address");
		final JFrame frame = new JFrame("New Address");

		final JTextField LnameField = new HintTextField("Last Name", false);
		final JTextField FnameField = new HintTextField("First Name(s)", false);
		final JTextField addressField = new HintTextField("Address", false);
		final JTextField cityField = new HintTextField("City", false);
		cityField.setColumns(15);
		final JComboBox<String> states = new JComboBox<>(ApplicationLiterals.STATE_CODES);
		states.setRenderer(new PromptComboBoxRenderer("Select State"));
		states.setSelectedIndex(-1);
		states.setMaximumRowCount(12);
		states.setFont(ApplicationLiterals.APP_FONT);

		final JTextField zipField = new HintTextField("Zip Code", false);

		final JButton insert = new PrimaryButton("    Insert    ");
		final JButton close = new PrimaryButton("    Close    ");

		final JLabel missingField = new JLabel();
		missingField.setForeground(Color.RED);
		missingField.setVisible(false);

		JPanel grid = new JPanel();
		grid.setLayout(new GridLayout(3, 2, 10, 15));
		grid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		grid.add(LnameField);
		grid.add(FnameField);
		grid.add(addressField);
		grid.add(cityField);
		grid.add(states);
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

		close.addActionListener(e -> frame.dispose());

		insert.addActionListener(e -> {
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

			else if (cityField.getText().trim().equals(ApplicationLiterals.EMPTY)) {
				missingField.setText("City cannot be blank");
				missingField.setVisible(true);
				frame.pack();
			}

			else if (states.getSelectedIndex() == -1) {
				missingField.setText("Please select a state");
				missingField.setVisible(true);
				frame.pack();
			}

			else if (zipField.getText().trim().equals(ApplicationLiterals.EMPTY)) {
				missingField.setText("Zip code cannot be blank");
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

				int recordCount = Addresses.newAddress(address);
				MainMenu.closeWindow();
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
					MainMenu.modeSelection(false, 3
					);
				}
			}
		});
	}
}

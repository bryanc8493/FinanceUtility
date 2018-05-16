package com.bryan.finance.gui.reminder;

import com.bryan.finance.beans.Reminder;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.literals.Icons;

import javax.swing.*;
import java.awt.*;

class ReminderRecord {

    private JTextField id = new JTextField();
    private JTextField title = new JTextField();
    private JTextField notes = new JTextField();
    private JTextField date = new JTextField();
    private JTextField dismissed = new JTextField();

    ReminderRecord(Reminder reminder) {
        final JFrame frame = new JFrame("Selected Reminder");

        JLabel idLabel = new JLabel("Reminder ID:");
        JLabel titleLabel = new JLabel("Reminder Title:");
        JLabel dateLabel = new JLabel("Reminder Date:");
        JLabel notesLabel = new JLabel("Reminder Notes:");
        JLabel dismissedLabel = new JLabel("Reminder Dismissed:               ");

        JButton close = new PrimaryButton("Close");

        JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonHolder.add(close);
        buttonHolder.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        setValues(reminder);

        JPanel content = new JPanel(new GridLayout(5, 2, 5, 5));
        content.add(idLabel);
        content.add(id);
        content.add(titleLabel);
        content.add(title);
        content.add(dateLabel);
        content.add(date);
        content.add(notesLabel);
        content.add(notes);
        content.add(dismissedLabel);
        content.add(dismissed);

        JPanel full = new JPanel(new BorderLayout());
        full.add(content, BorderLayout.CENTER);
        full.add(buttonHolder, BorderLayout.SOUTH);

        frame.add(full);
        frame.setIconImage(Icons.APP_ICON.getImage());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JRootPane rp = SwingUtilities.getRootPane(close);
        rp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rp.setDefaultButton(close);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        close.addActionListener((e) -> frame.dispose());
    }

    private void setValues(Reminder reminder) {

        id.setText(reminder.getId());
        title.setText(reminder.getText());
        date.setText(String.valueOf(reminder.getDate()));
        notes.setText(reminder.getNotes());
        dismissed.setText(setDismissedText(reminder.getDismissed()));

        id.setEditable(false);
        title.setEditable(false);
        date.setEditable(false);
        notes.setEditable(false);
        dismissed.setEditable(false);
    }

    private String setDismissedText(String dismissed) {
        if (dismissed.equalsIgnoreCase("F"))
            return "False";
        return "True";
    }
}

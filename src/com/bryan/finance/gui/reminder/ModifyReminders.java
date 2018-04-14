package com.bryan.finance.gui.reminder;

import com.bryan.finance.beans.Reminder;
import com.bryan.finance.database.Connect;
import com.bryan.finance.database.queries.QueryUtil;
import com.bryan.finance.database.queries.Transactions;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.MainMenu;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.program.FinanceUtility;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ModifyReminders {

    private JFrame frame = new JFrame("Modify Reminders");
    private Set<JCheckBox> records;
    private Logger logger = Logger.getLogger(ModifyReminders.class);

    public ModifyReminders(boolean fromCommandArg) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        JLabel label = new Title("Select Reminders To Dismiss");
        label.setBorder(ApplicationLiterals.PADDED_SPACE);

        JButton save = new PrimaryButton("Save");
        JButton close = new PrimaryButton("Close");
        JPanel button = new JPanel(new FlowLayout(FlowLayout.CENTER));
        button.add(close);
        button.add(save);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(label, BorderLayout.NORTH);
        p.add(getReminderDataForEditing(), BorderLayout.CENTER);
        p.add(button, BorderLayout.SOUTH);
        frame.setIconImage(Icons.APP_ICON.getImage());
        frame.add(p);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        close.addActionListener((e) -> frame.dispose());

        save.addActionListener(e ->  {
            Set<Reminder> reminders = new HashSet<>();

            for (JCheckBox box : records) {
                if (box.isSelected()) {
                    Reminder reminder = new Reminder();
                    String boxText = box.getText();
                    String idString = boxText.substring(boxText.indexOf("(")+1, boxText.indexOf(")"));
                    reminder.setId(idString);
                    reminders.add(reminder);
                }
            }

            logger.debug("Dismissing " + reminders.size() + " reminders");
            QueryUtil.dismissReminders(reminders);

            if(fromCommandArg) {
                logger.info("User Dismissed reminders");
                FinanceUtility.appLogger.logFooter();
                System.exit(0);
            } else {
                frame.dispose();
                MainMenu.closeWindow();
                MainMenu.modeSelection(false, 4);
            }
        });
    }

    private JPanel getReminderDataForEditing() {
        records = new LinkedHashSet<>();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();

            String SQL_TEXT = "SELECT ID, TITLE, DATE "
                    + "from " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                    + Tables.REMINDERS
                    + " where DISMISSED = 'F'";
            ResultSet rs = statement.executeQuery(SQL_TEXT);

            while (rs.next()) {
                String id = rs.getString(1);
                String title = rs.getString(2);
                String date = rs.getString(3);

                JCheckBox box = new JCheckBox();
                box.setText("(" + id + ") " + title + "  |  " + date);
                records.add(box);
            }

            for (JCheckBox x : records) {
                panel.add(x);
            }

            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        } catch (Exception e) {
            throw new AppException(e);
        }
        return panel;
    }
}

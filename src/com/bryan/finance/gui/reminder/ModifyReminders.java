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
    private Logger logger = Logger.getLogger(ModifyReminders.class);

    public ModifyReminders(boolean fromCommandArg) {
        if(QueryUtil.getTotalActiveRemindersToNotify() == 0 && fromCommandArg) {
            logger.info("no reminders to display");
            FinanceUtility.appLogger.logFooter();
            System.exit(0);
        }

        JPanel p = new JPanel(new BorderLayout(10, 0));
        JLabel label = new Title("Select Reminders To Dismiss");
        label.setBorder(ApplicationLiterals.PADDED_SPACE);

        Set<JCheckBox> activeCheckboxes = QueryUtil.getReminderCheckboxesForEditing(true);
        Set<JCheckBox> futureCheckboxes = QueryUtil.getReminderCheckboxesForEditing(false);

        JPanel reminderTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if(activeCheckboxes.size() > 0) {
            reminderTop.add(renderReminderData(activeCheckboxes));
            reminderTop.setBorder(BorderFactory.createCompoundBorder(
                    ApplicationLiterals.PADDED_SPACE,
                    BorderFactory.createTitledBorder("Active Reminders")));
        }

        JPanel reminderBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        if(futureCheckboxes.size() > 0) {
            reminderBottom.add(renderReminderData(futureCheckboxes));
            reminderBottom.setBorder(BorderFactory.createCompoundBorder(
                    ApplicationLiterals.PADDED_SPACE,
                    BorderFactory.createTitledBorder("Future Reminders")));
        }

        JPanel reminderContent = new JPanel(new BorderLayout(0,10));
        reminderContent.add(reminderTop, BorderLayout.NORTH);
        if(!fromCommandArg) {
            reminderContent.add(reminderBottom, BorderLayout.SOUTH);
        }

        JButton save = new PrimaryButton("Save");
        JButton close = new PrimaryButton("Close");
        JPanel button = new JPanel(new FlowLayout(FlowLayout.CENTER));
        button.add(close);
        button.add(save);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p.add(label, BorderLayout.NORTH);
        p.add(reminderContent, BorderLayout.CENTER);
        p.add(button, BorderLayout.SOUTH);
        frame.setIconImage(Icons.APP_ICON.getImage());
        frame.add(p);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        close.addActionListener((e) -> {
            frame.dispose();
            if(fromCommandArg) {
                FinanceUtility.appLogger.logFooter();
                System.exit(0);
            }
        });

        save.addActionListener(e ->  {
            Set<Reminder> reminders = getCheckedRemindersToDismiss(activeCheckboxes, futureCheckboxes);

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

    private JPanel renderReminderData(Set<JCheckBox> data) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (JCheckBox c : data) {
            panel.add(c);
        }
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return panel;
    }

    private Set<Reminder> getCheckedRemindersToDismiss(Set<JCheckBox> active, Set<JCheckBox> future) {
        Set<Reminder> reminders = new HashSet<>();

        for (JCheckBox box : active) {
            if (box.isSelected()) {
                Reminder reminder = new Reminder();
                String boxText = box.getText();
                String idString = boxText.substring(boxText.indexOf("(")+1, boxText.indexOf(")"));
                reminder.setId(idString);
                reminders.add(reminder);
            }
        }

        for (JCheckBox box : future) {
            if (box.isSelected()) {
                Reminder reminder = new Reminder();
                String boxText = box.getText();
                String idString = boxText.substring(boxText.indexOf("(")+1, boxText.indexOf(")"));
                reminder.setId(idString);
                reminders.add(reminder);
            }
        }

        return reminders;
    }
}

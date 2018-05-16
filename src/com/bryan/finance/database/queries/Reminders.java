package com.bryan.finance.database.queries;

import com.bryan.finance.beans.Reminder;
import com.bryan.finance.database.Connect;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.enums.Views;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class Reminders {

    private static Logger logger = Logger.getLogger(Reminders.class);

    public static void addReminder(Reminder reminder) {
        logger.debug("running query");
        try {
            Connection con = Connect.getConnection();

            PreparedStatement ps;
            String SQL_TEXT = "INSERT INTO " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                    + Tables.REMINDERS  + " (TITLE, DATE, DISMISSED, NOTES) "
                    + "VALUES (?, ?, ?, ?)";

            ps = con.prepareStatement(SQL_TEXT);
            ps.setString(1, reminder.getText());
            ps.setObject(2, reminder.getDate());
            ps.setString(3, reminder.getDismissed());
            ps.setString(4, reminder.getNotes());
            ps.executeUpdate();

            con.close();
            JOptionPane.showMessageDialog(null, "Reminder added!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    private static int getTotalDismissedReminders() {
        String SQL_TEXT = "SELECT COUNT(*) FROM " + Databases.ACCOUNTS
            + ApplicationLiterals.DOT + Views.DISMISSED_REMINDERS;

        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static int getTotalActiveReminders() {
        String SQL_TEXT = "SELECT COUNT(*) FROM " + Databases.ACCOUNTS
                + ApplicationLiterals.DOT + Views.ACTIVE_REMINDERS;

        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    private static int getTotalFutureReminders() {
        String SQL_TEXT = "SELECT COUNT(*) FROM " + Databases.ACCOUNTS
                + ApplicationLiterals.DOT + Views.FUTURE_REMINDERS;

        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static int getTotalNonDismissedReminders() {
        String SQL_TEXT = "SELECT COUNT(*) FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                + Tables.REMINDERS + " WHERE DISMISSED = 'F'";

        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getInt(1);
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static Object[][] getActiveReminders() {
        Object[][] records = new Object[getTotalActiveReminders()][3];
        String SQL_TEXT = "SELECT * FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT + Views.ACTIVE_REMINDERS;
        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            int recordCount = 0;
            while (rs.next()) {
                records[recordCount][0] = rs.getString(1);
                records[recordCount][1] = rs.getString(2);
                records[recordCount][2] = rs.getString(3);
                recordCount++;
            }
        } catch (SQLException e1) {
            throw new AppException(e1);
        }

        return records;
    }

    public static Object[][] getFutureReminders() {
        Object[][] records = new Object[getTotalFutureReminders()][3];
        String SQL_TEXT = "SELECT * FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT + Views.FUTURE_REMINDERS;
        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            int recordCount = 0;
            while (rs.next()) {
                records[recordCount][0] = rs.getString(1);
                records[recordCount][1] = rs.getString(2);
                records[recordCount][2] = rs.getString(3);
                recordCount++;
            }
        } catch (SQLException e1) {
            throw new AppException(e1);
        }

        return records;
    }

    public static Object[][] getDismissedReminders() {
        Object[][] records = new Object[getTotalDismissedReminders()][2];
        String SQL_TEXT = "SELECT * FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT + Views.DISMISSED_REMINDERS;
        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            int recordCount = 0;
            while (rs.next()) {
                records[recordCount][0] = rs.getString(1);
                records[recordCount][1] = rs.getString(2);
                recordCount++;
            }
        } catch (SQLException e1) {
            throw new AppException(e1);
        }

        return records;
    }

    public static void dismissReminders(Set<Reminder> reminders) {
        try {
            Connection con = Connect.getConnection();

            PreparedStatement ps;
            String SQL_TEXT = "UPDATE " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                    + Tables.REMINDERS + " SET DISMISSED = 'T' WHERE ID = ?";

            for (Reminder r : reminders) {
                ps = con.prepareStatement(SQL_TEXT);
                ps.setString(1, r.getId());
                ps.executeUpdate();
            }

            con.close();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static Set<JCheckBox> getReminderCheckboxesForEditing(boolean onlyActive) {
        Set<JCheckBox> records = new LinkedHashSet<>();

        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();

            String SQL_TEXT;
            if (onlyActive) {
                SQL_TEXT = "SELECT ID, TITLE, DATE "
                        + "from " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                        + Tables.REMINDERS
                        + " where DISMISSED = 'F' AND DATE <= now() ORDER BY DATE ASC";
            } else {
                SQL_TEXT = "SELECT ID, TITLE, DATE "
                        + "from " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                        + Tables.REMINDERS
                        + " where DISMISSED = 'F' AND DATE > now() ORDER BY DATE ASC";
            }
            ResultSet rs = statement.executeQuery(SQL_TEXT);

            while (rs.next()) {
                String id = rs.getString(1);
                String title = rs.getString(2);
                String date = rs.getString(3);

                JCheckBox box = new JCheckBox();
                box.setText("(" + id + ") " + title + "  |  " + date);
                records.add(box);
            }
            return records;

        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static Reminder getReminder(String Id) {
        String SQL_TEXT = "SELECT * FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                + Tables.REMINDERS + " WHERE ID = " + Id;
        Statement statement;
        ResultSet rs;
        Reminder reminder = new Reminder();
        try {
            Connection con = Connect.getConnection();
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            while (rs.next()) {
                reminder.setId(rs.getString(1));
                reminder.setText(rs.getString(2));
                reminder.setDate(rs.getDate(3));
                String dismissed = rs.getString(4);
                boolean isDismissed = dismissed.equalsIgnoreCase("T");
                reminder.setIsDismissed(isDismissed);
                reminder.setNotes(rs.getString(5));
            }
            con.close();
        } catch (Exception sqlE) {
            throw new AppException(sqlE);
        }
        return reminder;
    }
}

package com.bryan.finance.database.queries;

import com.bryan.finance.beans.Reminder;
import com.bryan.finance.beans.Salary;
import com.bryan.finance.database.Connect;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.sql.*;
import java.text.ParseException;
import java.util.*;
import java.util.Date;

public class QueryUtil {

    private static final Logger logger = Logger.getLogger(QueryUtil.class);

    public static int getMonthsSinceJan2016() {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(new Date());
        Calendar endCalendar = new GregorianCalendar();
        try {
            endCalendar.setTime(ApplicationLiterals.YEAR_MONTH_DAY.parse("2016-01-01"));
        } catch (ParseException e) {
            throw new AppException(e);
        }

        int diffYear = endCalendar.get(Calendar.YEAR)
                - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH)
                - startCalendar.get(Calendar.MONTH);
        return diffMonth * -1;
    }

    public static Set<Salary> getSalaryData() {
        logger.debug("Getting all salary data");
        String SQL_TEXT = "SELECT * FROM " + Databases.FINANCIAL + ApplicationLiterals.DOT
                + Tables.PAY_GRADES;
        Set<Salary> data = new LinkedHashSet<>();

        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            while (rs.next()) {
                Salary salary = new Salary();
                salary.setGrade(rs.getInt(1));
                salary.setMinPay(rs.getInt(2));
                salary.setMidPay(rs.getInt(3));
                salary.setMaxPay(rs.getInt(4));
                salary.setStiTarget(rs.getInt(5));
                salary.setStiMax(rs.getInt(6));
                salary.setMtiTarget(rs.getInt(7));
                salary.setMtiMax(rs.getInt(8));
                data.add(salary);
            }
            con.close();
        } catch (Exception e) {
            throw new AppException(e);
        }
        return data;
    }

    public static void addReminder(Reminder reminder) {
        logger.debug("running query");
        try {
            Connection con = Connect.getConnection();

            PreparedStatement ps;
            String SQL_TEXT = "INSERT INTO " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                    + Tables.REMINDERS  + " (TITLE, DATE, DISMISSED) "
                    + "VALUES (?, ?, ?)";

            ps = con.prepareStatement(SQL_TEXT);
            ps.setString(1, reminder.getText());
            ps.setObject(2, reminder.getDate());
            ps.setString(3, reminder.getDismissed());
            ps.executeUpdate();

            con.close();
            JOptionPane.showMessageDialog(null, "Reminder added!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static int getTotalActiveReminders() {
        logger.debug("Getting all active reminders");
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

    public static int getTotalActiveRemindersToNotify() {
        logger.debug("Getting all active reminders");
        String SQL_TEXT = "SELECT COUNT(*) FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                + Tables.REMINDERS + " WHERE DISMISSED = 'F' AND DATE <= now()";

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

    public static Object[][] getReminders() {
        Object[][] records = new Object[getTotalActiveReminders()][2];
        String SQL_TEXT = "SELECT TITLE, DATE FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                + Tables.REMINDERS + " WHERE DISMISSED = 'F' ORDER BY DATE ASC";
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
            String SQL_TEXT = "DELETE FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                    + Tables.REMINDERS + " WHERE ID = ?";

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
}

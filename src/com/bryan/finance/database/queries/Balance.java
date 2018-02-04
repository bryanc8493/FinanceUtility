package com.bryan.finance.database.queries;

import com.bryan.finance.database.Connect;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Balance {

    private static Logger logger = Logger.getLogger(Balance.class);

    public static String getFutureBalance() {
        logger.debug("Getting future Balance...");
        final Connection con = Connect.getConnection();

        String SQL_TEXT = "SELECT SUM(COMBINED_AMOUNT) FROM " +
                Databases.FINANCIAL + ApplicationLiterals.DOT + Tables.MONTHLY_TRANSACTIONS +
                " where CREDIT_PAID <> '0'";
        Statement statement;
        ResultSet rs;
        try {
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getString(1);
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    public static String getTodaysBalance() {
        logger.debug("Getting today's Balance...");
        final Connection con = Connect.getConnection();

        String SQL_TEXT = "SELECT SUM(COMBINED_AMOUNT) FROM "
                + Databases.FINANCIAL + ApplicationLiterals.DOT + Tables.MONTHLY_TRANSACTIONS
                + " where TRANSACTION_DATE <= now() "
                + "AND CREDIT_PAID <> '0'";
        Statement statement;
        ResultSet rs;
        try {
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getString(1);
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    public static String getTrueBalance() {
        logger.debug("Getting true Balance...");
        final Connection con = Connect.getConnection();

        String SQL_TEXT = "SELECT SUM(COMBINED_AMOUNT) FROM " +
                Databases.FINANCIAL + ApplicationLiterals.DOT + Tables.MONTHLY_TRANSACTIONS;
        Statement statement;
        ResultSet rs;
        try {
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getString(1);
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    public static String getCreditBalance() {
        logger.debug("Getting future Balance...");
        final Connection con = Connect.getConnection();

        String SQL_TEXT = "SELECT SUM(AMOUNT) FROM "
                + Databases.FINANCIAL + ApplicationLiterals.DOT + Tables.MONTHLY_TRANSACTIONS
                + " WHERE CREDIT = '1' AND CREDIT_PAID = '0'";
        Statement statement;
        ResultSet rs;
        try {
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getString(1);
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    public static double getSavingsBalance() {
        logger.debug("Getting savings balance...");
        final Connection con = Connect.getConnection();

        String SQL_TEXT = "SELECT SUM(SUM_AMOUNT) FROM "
            + Databases.FINANCIAL + ApplicationLiterals.DOT + Tables.SAVINGS;
        Statement statement;
        ResultSet rs;
        try {
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }
}

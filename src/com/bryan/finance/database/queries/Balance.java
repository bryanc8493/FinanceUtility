package com.bryan.finance.database.queries;

import com.bryan.finance.database.Connect;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.enums.Views;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;
import org.apache.log4j.Logger;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Balance {

    private static Logger logger = Logger.getLogger(Balance.class);

    private static final String TRANSACTION_TABLE = ""+ Databases.FINANCIAL + ApplicationLiterals.DOT + Tables.MONTHLY_TRANSACTIONS;
    private static final String SAVINGS_TABLE = ""+ Databases.FINANCIAL + ApplicationLiterals.DOT + Tables.SAVINGS;

    public static String getFutureBalance() {
        logger.debug("Getting future Balance...");
        final Connection con = Connect.getConnection();

        String SQL_TEXT = "SELECT TOTAL FROM " + Databases.FINANCIAL + ApplicationLiterals.DOT + Views.FUTURE_BALANCE_SUM;
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

        String query = "SELECT BALANCE FROM " + Databases.FINANCIAL + ApplicationLiterals.DOT
                + Views.CURRENT_BALANCE_TODAY;
        Statement statement;
        ResultSet rs;
        try {
            statement = con.createStatement();
            rs = statement.executeQuery(query);
            rs.next();
            return rs.getString(1);
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    public static String getTrueBalance() {
        logger.debug("Getting true Balance...");
        final Connection con = Connect.getConnection();

        String SQL_TEXT = "SELECT TRUE_BALANCE FROM " + Databases.FINANCIAL + ApplicationLiterals.DOT + Views.TRUE_BALANCE;
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
        logger.debug("Getting credit Balance...");
        final Connection con = Connect.getConnection();

        String SQL_TEXT = "SELECT TOTAL FROM " + Databases.FINANCIAL + ApplicationLiterals.DOT + Views.UNPAID_CREDITS_SUM;
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

        String SQL_TEXT = "SELECT BALANCE FROM " + Databases.FINANCIAL + ApplicationLiterals.DOT + Views.SAVINGS_BALANCE;
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

    public static String getFuturePayments() {
        logger.debug("Determining Future Payments...");
        final Connection con = Connect.getConnection();
        String SQL_TEXT = "SELECT FUTURE_SUM FROM " + Databases.FINANCIAL + ApplicationLiterals.DOT + Views.FUTURE_TRANSACTIONS_SUM;
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
}

package com.bryan.finance.database.queries;

import com.bryan.finance.config.ReadConfig;
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


public class Transactions {

    private static Logger logger = Logger.getLogger(Transactions.class);

    public static Object[][] getPastEntries() {
        final Connection con = Connect.getConnection();
        int entriesToRetrieve = Integer.parseInt(ReadConfig
                .getConfigValue(ApplicationLiterals.VIEWING_AMOUNT_MAX));
        Object[][] records = new Object[entriesToRetrieve][5];
        logger.debug("Getting past " + entriesToRetrieve + " transaction records...");

        String SQL_TEXT = "SELECT TRANSACTION_ID, TITLE, TYPE, TRANSACTION_DATE, AMOUNT FROM "
                + Databases.FINANCIAL + ApplicationLiterals.DOT + Tables.MONTHLY_TRANSACTIONS
                + " ORDER BY TRANSACTION_ID desc limit " + entriesToRetrieve;
        Statement statement;
        ResultSet rs;
        int recordCount = 0;

        try {
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);

            while (rs.next()) {
                for (int i=0; i<5; i++) {
                    records[recordCount][i] = rs.getString(i+1);
                }
                recordCount++;
            }
        } catch (SQLException e) {
            throw new AppException(e);
        }
        return records;
    }

    public static Object[][] getMonthlySummaries() {
        logger.debug("Generating Monthly Summaries...");
        final Connection con = Connect.getConnection();
        Object[][] records = new Object[QueryUtil.getMonthsSinceJan2016()][5];

        String SQL_TEXT = "SELECT MONTH, YEAR, TOTAL_EXPENSES, TOTAL_INCOME, MONTHLY_CASH_FLOW FROM "
            + Databases.FINANCIAL + ApplicationLiterals.DOT + Tables.MONTHLY_TOTALS
            + " ORDER BY ID DESC";
        Statement statement;
        ResultSet rs;
        int recordCount = 0;
        try {
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);

            while (rs.next()) {
                records[recordCount][0] = rs.getString(1);
                records[recordCount][1] = rs.getString(2);
                records[recordCount][2] = "$  " + rs.getString(3);
                records[recordCount][3] = "$  " + rs.getString(4);
                String tempCashFlow = rs.getString(5);
                if (Double.parseDouble(tempCashFlow) >= 0.00) {
                    records[recordCount][4] = "$  " + tempCashFlow;
                } else {
                    records[recordCount][4] = "$  ( " + tempCashFlow + " )";
                }

                recordCount++;
            }
        } catch (SQLException e) {
            throw new AppException(e);
        }
        return records;
    }
}

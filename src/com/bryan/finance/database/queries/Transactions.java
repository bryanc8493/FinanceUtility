package com.bryan.finance.database.queries;

import com.bryan.finance.beans.Transaction;
import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.database.Connect;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Set;


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

    public static Transaction getSpecifiedTransaction(String tranId) {
        String SQL_TEXT = "SELECT TRANSACTION_ID, TITLE, TYPE, CATEGORY, TRANSACTION_DATE, AMOUNT, DESCRIPTION, CREDIT, CREDIT_PAID "
                + "FROM " + Databases.FINANCIAL + ApplicationLiterals.DOT
                + Tables.MONTHLY_TRANSACTIONS + " WHERE TRANSACTION_ID = " + tranId;
        Statement statement;
        ResultSet rs;
        Transaction tran = new Transaction();
        try {
            Connection con = Connect.getConnection();
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            while (rs.next()) {
                tran.setTransactionID(rs.getString(1));
                tran.setTitle(rs.getString(2));
                tran.setType(rs.getString(3));
                tran.setCategory(rs.getString(4));
                tran.setDate(rs.getString(5));
                tran.setAmount(rs.getString(6));
                tran.setDescription(rs.getString(7));
                tran.setCredit(rs.getString(8).charAt(0));
                tran.setCreditPaid(rs.getString(9).charAt(0));
            }
            con.close();
        } catch (StringIndexOutOfBoundsException e) {
            tran.setCreditPaid(' ');
        } catch (SQLException sqlE) {
            throw new AppException(sqlE);
        }
        return tran;
    }

    public static void markCreditsPaid(Set<Transaction> records) {
        try {
            Connection con = Connect.getConnection();

            PreparedStatement ps;
            String SQL_TEXT = "UPDATE " + Databases.FINANCIAL + ApplicationLiterals.DOT
                    + Tables.MONTHLY_TRANSACTIONS + " SET CREDIT_PAID = '1' "
                    + "where TRANSACTION_ID = ?";

            for (Transaction t : records) {
                ps = con.prepareStatement(SQL_TEXT);
                ps.setString(1, t.getTransactionID());
                ps.executeUpdate();
            }

            con.close();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    private static int getFutureRecordCount() {
        String SQL_TEXT = "SELECT COUNT(*) FROM " + Databases.FINANCIAL
            + ApplicationLiterals.DOT + Tables.MONTHLY_TRANSACTIONS
            + " WHERE TRANSACTION_DATE > now()";

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

    public static Object[][] getFutureRecords() {
        Object[][] data = new Object[getFutureRecordCount()][5];
        String SQL_TEXT = "SELECT TITLE, TYPE, CATEGORY, TRANSACTION_DATE, AMOUNT "
                + "FROM " + Databases.FINANCIAL
                + ApplicationLiterals.DOT + Tables.MONTHLY_TRANSACTIONS
                + " WHERE TRANSACTION_DATE > now()";
        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            int count = 0;

            while (rs.next()) {
                data[count][0] = rs.getString(1);
                data[count][1] = rs.getString(2);
                data[count][2] = rs.getString(3);
                data[count][3] = rs.getString(4);
                if(data[count][1].toString().equalsIgnoreCase("Expense")) {
                    data[count][4] = "( -" + rs.getString(5) + " )";
                }else{
                    data[count][4] = rs.getString(5);
                }

                count++;
            }
        } catch (Exception e) {
            throw new AppException(e);
        }
        return data;
    }

    private static int getNumberOfUnpaidCredits() {
        String SQL_TEXT = "SELECT COUNT(*) FROM " + Databases.FINANCIAL
            + ApplicationLiterals.DOT + Tables.MONTHLY_TRANSACTIONS
                + " where CREDIT = '1' AND CREDIT_PAID = '0'";

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

    public static Object[][] getUnpaidCreditRecords() {
        Object[][] data = new Object[getNumberOfUnpaidCredits()][4];
        String SQL_TEXT = "SELECT TITLE, CATEGORY, TRANSACTION_DATE, AMOUNT "
                + "FROM " + Databases.FINANCIAL
                + ApplicationLiterals.DOT + Tables.MONTHLY_TRANSACTIONS
                + " where CREDIT = '1' AND CREDIT_PAID = '0'";
        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            int count = 0;

            while (rs.next()) {
                for (int i=0; i<4; i++) {
                    data[count][i] = rs.getString(i+1);
                }
                count++;
            }
        } catch (Exception e) {
            throw new AppException(e);
        }
        return data;
    }

    public static void addTransaction(Transaction tran) {
        try {
            Connection con = Connect.getConnection();

            PreparedStatement ps;
            String SQL_TEXT = "INSERT INTO " + Databases.FINANCIAL + ApplicationLiterals.DOT
                    + Tables.MONTHLY_TRANSACTIONS  + " (TITLE, TYPE, CATEGORY, TRANSACTION_DATE, "
                    + "AMOUNT, DESCRIPTION, CREDIT, CREDIT_PAID) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            ps = con.prepareStatement(SQL_TEXT);
            ps.setString(1, tran.getTitle());
            ps.setString(2, tran.getType());
            ps.setString(3, tran.getCategory());
            ps.setString(4, tran.getDate());
            ps.setString(5, tran.getAmount());
            ps.setString(6, tran.getDescription());
            ps.setString(7, String.valueOf(tran.getCredit()));
            ps.setString(8, String.valueOf(tran.getCreditPaid()));
            ps.executeUpdate();

            if(tran.getCategory().equals(ApplicationLiterals.SAVINGS) ||
                    tran.getCategory().equals(ApplicationLiterals.SAVINGS_TRANSFER))
                addSavingsTransaction(tran, con);

            con.close();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    private static void addSavingsTransaction(Transaction tran, Connection con) {
        try {
            PreparedStatement ps;
            String SQL_TEXT = "INSERT INTO " + Databases.FINANCIAL + ApplicationLiterals.DOT
                    + Tables.SAVINGS  + " (TRANS_TYPE, TRANS_DATE, AMOUNT, DESCRIPTION, SUM_AMOUNT) "
                    + "VALUES (?, ?, ?, ?, ?)";

            String sumAmount = tran.getType().equals(ApplicationLiterals.INCOME) ?
                    "-" + tran.getAmount() :
                    tran.getAmount();
            String tranType = tran.getType().equals(ApplicationLiterals.EXPENSE) ?
                    ApplicationLiterals.INCOME :
                    ApplicationLiterals.EXPENSE;

            ps = con.prepareStatement(SQL_TEXT);
            ps.setString(1, tranType);
            ps.setString(2, tran.getDate());
            ps.setString(3, tran.getAmount());
            ps.setString(4, tran.getDescription());
            ps.setString(5, sumAmount);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new AppException(e);
        }
    }
}

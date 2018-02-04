package com.bryan.finance.database.queries;

import com.bryan.finance.beans.Account;
import com.bryan.finance.database.Connect;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.security.Encoding;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.*;

public class Accounts {

    private static Logger logger = Logger.getLogger(Accounts.class);

    public static Object[][] getAccounts() {
        logger.debug("Getting all accounts...");
        int totalAccounts = getTotalNumberOfAccounts();
        Object[][] records = new Object[totalAccounts][3];

        String key;
        try {
            key = Encoding.decrypt(ApplicationLiterals.getEncryptionKey());
        } catch (GeneralSecurityException | IOException e2) {
            throw new AppException(e2);
        }
        String SQL_TEXT = "SELECT ACCOUNT, USERNAME, AES_DECRYPT(PASS, '" + key
                + "') FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT + Tables.SITES
                + " order by ACCOUNT ASC";
        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            int recordCount = 0;

            while (rs.next()) {
                for (int i=0; i<3; i++) {
                    records[recordCount][i] = rs.getString(i+1);
                }
                recordCount++;
            }
        } catch (SQLException e1) {
            throw new AppException(e1);
        }
        return records;
    }

    public static Object[][] getFullAccounts() {
        logger.debug("Getting full accounts with password");
        int totalAccounts = getTotalNumberOfAccounts();
        Object[][] records = new Object[totalAccounts][4];

        String key;
        try {
            key = Encoding.decrypt(ApplicationLiterals.getEncryptionKey());
        } catch (GeneralSecurityException | IOException e2) {
            throw new AppException(e2);
        }
        String SQL_TEXT = "SELECT ID, ACCOUNT, USERNAME, AES_DECRYPT(PASS, '" + key
                + "') FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                + Tables.SITES + " ORDER BY ACCOUNT ASC";
        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            int recordCount = 0;

            while (rs.next()) {
                for (int i=0; i<4; i++) {
                    records[recordCount][i] = rs.getString(i+1);
                }
                recordCount++;
            }
        } catch (SQLException e1) {
            throw new AppException(e1);
        }
        return records;
    }

    public static int newAccount(Account account) throws Exception {
        final Connection con = Connect.getConnection();
        String acctName = account.getAccount();
        String username = account.getUsername();
        String pass = account.getPassword();

        logger.debug("Adding new account: " + acctName + " - username: "
                + username + " - password: *******");

        String SQL_TEXT = "INSERT INTO " + Databases.ACCOUNTS +
                ApplicationLiterals.DOT + Tables.SITES + " (ACCOUNT, USERNAME, PASS) VALUES('"
                + acctName
                + "', '"
                + username
                + "', AES_ENCRYPT('"
                + pass
                + "', "
                + "'"
                + Encoding.decrypt(ApplicationLiterals.getEncryptionKey())
                + "'))";
        PreparedStatement ps;

        try {
            ps = con.prepareStatement(SQL_TEXT);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    private static int getTotalNumberOfAccounts() {
        final Connection con = Connect.getConnection();
        String SQL_TEXT = "SELECT COUNT(*) FROM "
                + Databases.ACCOUNTS + ApplicationLiterals.DOT + Tables.SITES;
        Statement statement;
        ResultSet rs;
        try {
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }
}

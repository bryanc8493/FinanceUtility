package com.bryan.finance.database.queries;

import com.bryan.finance.beans.Account;
import com.bryan.finance.beans.User;
import com.bryan.finance.database.Connect;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.security.Encoding;
import com.bryan.finance.utilities.Helpers;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Accounts {

    private static Logger logger = Logger.getLogger(Accounts.class);

    public static String getUrl(String account) {
        logger.debug("Getting login URL for account: " + account);
        Statement statement;
        ResultSet rs;
        String SQL_TEXT = "SELECT URL FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                + Tables.SITES + " WHERE ACCOUNT = '" + account + "'";

        try {
            Connection con = Connect.getConnection();
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getString(1);
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static String getPassword(String account) {
        logger.debug("Getting password for account: " + account);
        Statement statement;
        ResultSet rs;
        String key;
        try {
            key = Encoding.decrypt(ApplicationLiterals.getEncryptionKey());
        } catch(Exception e) {
            throw new AppException(e);
        }
        String SQL_TEXT = "SELECT AES_DECRYPT(PASS, '" + key + "') FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                + Tables.SITES + " WHERE ACCOUNT = '" + account + "'";

        try {
            Connection con = Connect.getConnection();
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            return rs.getString(1);
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

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
        Object[][] records = new Object[totalAccounts][5];

        String key;
        try {
            key = Encoding.decrypt(ApplicationLiterals.getEncryptionKey());
        } catch (GeneralSecurityException | IOException e2) {
            throw new AppException(e2);
        }
        String SQL_TEXT = "SELECT ID, ACCOUNT, USERNAME, AES_DECRYPT(PASS, '" + key
                + "'), URL FROM " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                + Tables.SITES + " ORDER BY ACCOUNT ASC";
        try {
            Connection con = Connect.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            int recordCount = 0;

            while (rs.next()) {
                for (int i=0; i<5; i++) {
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
        String url =  account.getUrl();

        logger.debug("Adding new account: " + acctName + " - username: "
                + username + " - password: ******* - login url: " + url);

        String SQL_TEXT = "INSERT INTO " + Databases.ACCOUNTS +
                ApplicationLiterals.DOT + Tables.SITES + " (ACCOUNT, USERNAME, PASS, URL) VALUES('"
                + acctName
                + "', '"
                + username
                + "', AES_ENCRYPT('"
                + pass
                + "', "
                + "'"
                + Encoding.decrypt(ApplicationLiterals.getEncryptionKey())
                + "'), '" + url + "')";
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

    public static boolean wasUserPasswordReset(String user) {
        final Connection con = Connect.getConnection();
        String SQL_TEXT = "SELECT MUST_CHANGE_PASS FROM "
                + Databases.ACCOUNTS + ApplicationLiterals.DOT + Tables.USERS
                + " WHERE USERNAME = UPPER('" + user + "')";
        Statement statement;
        ResultSet rs;
        try {
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            String value = rs.getString(1);
            return value.equals("1");
        } catch (Exception e) {
            return false;
        }
    }

    public static void removeMustChangeFlag(String user) {
        final Connection con = Connect.getConnection();
        String SQL_TEXT = "UPDATE "
                + Databases.ACCOUNTS + ApplicationLiterals.DOT + Tables.USERS
                + " SET MUST_CHANGE_PASS = null, LOGIN_BEFORE_LOCK = null "
                + "WHERE USERNAME = UPPER('" + user +"')";
        PreparedStatement ps;

        try {
            ps = con.prepareStatement(SQL_TEXT);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static boolean isResetTimerValid(String user) {
        final Connection con = Connect.getConnection();
        String SQL_TEXT = "SELECT DISTINCT CASE WHEN "
            + "((SELECT LOGIN_BEFORE_LOCK FROM "
            + Databases.ACCOUNTS + ApplicationLiterals.DOT + Tables.USERS
            + " WHERE USERNAME = UPPER('" + user + "')) > now()) "
            + " THEN '1' ELSE '0' END AS RESULT FROM "
            + Databases.ACCOUNTS + ApplicationLiterals.DOT + Tables.USERS;

        Statement statement;
        ResultSet rs;

        try {
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            String value = rs.getString(1);
            return value.equals("1");
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static int newUser(User user) throws Exception {
        logger.debug("Creating new user: " + user.getUsername());
        final Connection con = Connect.getConnection();
        String SQL_TEXT = "INSERT INTO " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                + Tables.USERS + " VALUES('"
                + user.getUsername() + "', '" + user.getEmail() + "', "
                + "AES_ENCRYPT('" + user.getPassword() + "', '"
                + Encoding.decrypt(ApplicationLiterals.getEncryptionKey())
                + "'), now(), " + "'" + user.getPermission() + "', '"
                + user.getStatus() + "')";

        PreparedStatement ps;
        try {
            ps = con.prepareStatement(SQL_TEXT);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    public static Set<User> getAllUsers() {
        Set<User> users = new LinkedHashSet<>();
        logger.debug("Getting all users...");
        String SQL_TEXT = "SELECT * FROM " + Databases.ACCOUNTS
                + ApplicationLiterals.DOT + Tables.USERS + " where USERNAME <> 'ROOT'";
        Statement statement;
        ResultSet rs;

        try {
            Connection con = Connect.getConnection();
            statement = con.createStatement();
            rs = statement.executeQuery(SQL_TEXT);
            while (rs.next()) {
                User u = new User();
                u.setUsername(rs.getString(1));
                u.setEmail(rs.getString(2));
                u.setLastLogin(rs.getString(4));
                u.setPermission(rs.getString(5).charAt(0));
                u.setStatus(rs.getString(6));
                users.add(u);
            }
            con.close();
        } catch (Exception e) {
            throw new AppException(e);
        }
        return users;
    }

    public static void lockUser(String user) {
        try {
            Connection con = Connect.getConnection();
            String[] systemInfo = Helpers.getSystemInfo();

            PreparedStatement ps;
            String SQL_TEXT = ("INSERT INTO " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                    + Tables.BANNED_USERS + " VALUES('"
                    + systemInfo[0] + "'," + "'" + systemInfo[1] + "', '"
                    + systemInfo[2] + "', now(), '" + user + "')");
            logger.warn("Locking user: " + user);

            ps = con.prepareStatement(SQL_TEXT);
            ps.executeUpdate();

            SQL_TEXT = "UPDATE " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                    + Tables.USERS + " SET STATUS = 'LOCKED' where USERNAME = '"
                    + user + "'";
            ps = con.prepareStatement(SQL_TEXT);
            ps.executeUpdate();

            con.close();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static void unlockUser(String user) {
        try {
            Connection con = Connect.getConnection();

            PreparedStatement ps;
            String SQL_TEXT = "UPDATE " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                    + Tables.USERS + " SET STATUS = 'UNLOCKED' WHERE USERNAME = '"
                    + user + "'";
            logger.warn("Unlocking user: " + user);

            ps = con.prepareStatement(SQL_TEXT);
            ps.executeUpdate();

            con.close();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }

    public static int setNewPassword(String input) {
        logger.debug("Changing password for user: " + Connect.getCurrentUser());
        int recordsInserted = 0;

        try {
            String SQL_TEXT = "UPDATE " + Databases.ACCOUNTS + ApplicationLiterals.DOT + Tables.USERS
                    + " SET ENCRYPTED_PASS = AES_ENCRYPT('" + input + "', '"
                    + Encoding.decrypt(ApplicationLiterals.getEncryptionKey())
                    + "') " + " WHERE USERNAME = '" + Connect.getCurrentUser()
                    + "'";

            Connection con = Connect.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_TEXT);
            recordsInserted = ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            throw new AppException(e);
        }
        return recordsInserted;
    }

    public static int setNewPassword(String user, String input) {
        logger.debug("Changing password for user: " + user);
        int updateCount = 0;
        try {
            String SQL_TEXT = "UPDATE "
                    + Databases.ACCOUNTS + ApplicationLiterals.DOT + Tables.USERS
                    + " SET ENCRYPTED_PASS = AES_ENCRYPT('" + input + "', '"
                    + Encoding.decrypt(ApplicationLiterals.getEncryptionKey())
                    + "') " + " WHERE USERNAME = '" + user + "'";

            Connection con = Connect.getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_TEXT);
            updateCount = ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            throw new AppException(e);
        }
        return updateCount;
    }

    public static int resetPassword(String user, String pass) {
        int recordsInserted = 0;
        Connection con = null;
        char permission = ApplicationLiterals.FULL_ACCESS;
        try {
            con = Connect.getConnection();
            String SQL_TEXT = "SELECT PERMISSION FROM "
                    + Databases.ACCOUNTS + ApplicationLiterals.DOT + Tables.USERS
                    + " WHERE USERNAME = '"
                    + user + "'";

            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);
            rs.next();
            permission = rs.getString(1).charAt(0);
        } catch (Exception e) {
            logger.error("Failed getting user's permission" + e.toString()
                    + Arrays.toString(e.getStackTrace()));
        }

        if (permission == ApplicationLiterals.FULL_ACCESS) {
            JOptionPane.showMessageDialog(null,
                    "Password cannot be changed for this user", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                String SQL_TEXT = "UPDATE " + Databases.ACCOUNTS + ApplicationLiterals.DOT
                        + Tables.USERS
                        + " SET ENCRYPTED_PASS = AES_ENCRYPT('" + pass + "', '"
                        + Encoding.decrypt(ApplicationLiterals.getEncryptionKey()) + "'), "
                        + "LOGIN_BEFORE_LOCK = (now() + INTERVAL 10 MINUTE), "
                        + "MUST_CHANGE_PASS = '1' WHERE USERNAME = '" + user + "'";

                PreparedStatement ps = con.prepareStatement(SQL_TEXT);
                recordsInserted = ps.executeUpdate();
                logger.debug("reset password for user: " + user);
                con.close();
            } catch (Exception e) {
                throw new AppException(e);
            }
        }
        return recordsInserted;
    }

    public static boolean doesUsernameExist(String user) {
        try {
            Connection con = Connect.getConnection();

            String SQL_TEXT = ("SELECT USERNAME from " + Databases.ACCOUNTS
                    + ApplicationLiterals.DOT + Tables.USERS
                    + " WHERE USERNAME = UPPER('" + user + "')");

            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(SQL_TEXT);

            rs.next();
            try {
                rs.getString(1);
                return true;
            } catch (Exception e) {
                logger.warn("Username " + user + " does not exist."
                        + ApplicationLiterals.NEW_LINE
                        + "Try again or create new account");
                JOptionPane.showMessageDialog(null, "Username " + user
                                + " does not exist." + ApplicationLiterals.NEW_LINE
                                + "Try again or create new account!",
                        "Invalid User", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            throw new AppException(e);
        }
    }
}

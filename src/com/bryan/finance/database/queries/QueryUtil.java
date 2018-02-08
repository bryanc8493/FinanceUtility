package com.bryan.finance.database.queries;

import com.bryan.finance.beans.Salary;
import com.bryan.finance.database.Connect;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.enums.Tables;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.util.*;

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
}

package com.bryan.finance.database.queries;

import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class QueryUtil {

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
}

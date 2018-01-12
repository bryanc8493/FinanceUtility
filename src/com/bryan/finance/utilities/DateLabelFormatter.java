package com.bryan.finance.utilities;

import java.text.ParseException;
import java.util.Calendar;

import javax.swing.JFormattedTextField.AbstractFormatter;

import com.bryan.finance.literals.ApplicationLiterals;

public class DateLabelFormatter extends AbstractFormatter {

	private static final long serialVersionUID = 3364542258272715973L;

	@Override
	public Object stringToValue(String text) throws ParseException {
		return ApplicationLiterals.YEAR_MONTH_DAY.parseObject(text);
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value != null) {
			Calendar cal = (Calendar) value;
			return ApplicationLiterals.YEAR_MONTH_DAY.format(cal.getTime());
		}
		return "";
	}

}

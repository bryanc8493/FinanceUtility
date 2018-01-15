package com.bryan.finance.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;

public class ReadConfig extends ApplicationLiterals {

	public static String getConfigValue(String property) {

		String value;
		Properties prop = new Properties();
		InputStream input;

		try {
			input = new FileInputStream(getConfigFile(getLaunchPath()));
			prop.load(input);
			value = prop.getProperty(property);
		} catch (Exception e) {
			throw new AppException(e);
		}
		return value;
	}

	public static String getLaunchPath() {
		return System.getProperty(USER_DIR);
	}

	public static String getConfigFile(String dir) {
		return dir + "/config/config.properties";
	}

	public static Map<String, String> getAllProperties() {
		Map<String, String> properties = new HashMap<>();

		Properties props = new Properties();
		InputStream input;

		try {
			input = new FileInputStream(getConfigFile(getLaunchPath()));
			props.load(input);
		} catch (Exception e) {
			throw new AppException(e);
		}

		for (Entry<Object, Object> entry : props.entrySet()) {
			properties.put(entry.getKey().toString(), entry.getValue()
					.toString());
		}
		return properties;
	}
}

package com.bryan.finance.reporting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.bryan.finance.beans.ReportRecord;
import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.literals.ApplicationLiterals;

public class WriteReport {

	private static final String HEADER = "ID, TITLE, TYPE, CATEGORY, TRANS_DATE, AMOUNT, COMBINED_AMOUNT, DESCRIPTION";
	private static Logger logger = Logger.getLogger(WriteReport.class);
	private static String outputDir = ReadConfig
			.getConfigValue(ApplicationLiterals.REPORTS_OUTPUT_DIR);

	public static String createCSVOutput(List<ReportRecord> data, int year,
			int month) {
		String name = outputDir + "/monthly" + ApplicationLiterals.UNDERSCORE
				+ year + ApplicationLiterals.UNDERSCORE + month
				+ ApplicationLiterals.DOT_CSV;
		logger.debug("Creating Report: " + name);

		writeReport(data, name);

		return name;
	}

	public static String createCSVOutput(List<ReportRecord> data, String start,
			String end) {

		String name = outputDir + "/custom" + ApplicationLiterals.UNDERSCORE
				+ start + ApplicationLiterals.UNDERSCORE + "to"
				+ ApplicationLiterals.UNDERSCORE + end
				+ ApplicationLiterals.DOT_CSV;
		logger.debug("Creating Report: " + name);

		writeReport(data, name);

		return name;
	}

	private static void writeReport(List<ReportRecord> data, String name) {
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			fw = new FileWriter(name);
			bw = new BufferedWriter(fw);
			bw.write(HEADER + ApplicationLiterals.NEW_LINE);
			for (ReportRecord r : data) {
				bw.write(r.getID() + ApplicationLiterals.COMMA);
				bw.write(r.getTitle() + ApplicationLiterals.COMMA);
				bw.write(r.getType() + ApplicationLiterals.COMMA);
				bw.write(r.getCategory() + ApplicationLiterals.COMMA);
				bw.write(r.getDate() + ApplicationLiterals.COMMA);
				bw.write("\"" + r.getAmount() + "\",");
				bw.write("\"" + r.getCombinedAmount() + "\",");
				bw.write(r.getDescription() + ApplicationLiterals.NEW_LINE);
			}
			bw.flush();

		} catch (IOException e) {
			throw new AppException(e);
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				throw new AppException(ex);
			}
		}
	}
}

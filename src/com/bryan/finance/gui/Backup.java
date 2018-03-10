package com.bryan.finance.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.enums.Databases;
import com.bryan.finance.exception.AppException;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.security.Encoding;
import com.bryan.finance.utilities.DeleteDirectory;

class Backup {

	private final String BACKUP_DIR = ReadConfig
			.getConfigValue(ApplicationLiterals.MY_SQL_BACKUP);
	private final String MYSQL_DIR = ReadConfig
			.getConfigValue(ApplicationLiterals.MY_SQL_DIR);

	Backup() {
		final JFrame frame = new JFrame("Backup Status");
		JLabel title = new Title("Backup Status");

		String lastBackupTime = getLastBackupTime(ApplicationLiterals.FULL_DATE);

		JLabel backupTime = new JLabel("<html><b>Last Backed Up:</b>&emsp;"
				+ lastBackupTime + "</html>", SwingConstants.CENTER);
		backupTime.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JButton close = new PrimaryButton("Close");
		JButton backup = new PrimaryButton("Backup Now");

		JPanel buttons = new JPanel(new FlowLayout());
		buttons.add(backup);
		backup.setVisible(enableBackup(ApplicationLiterals.YEAR_MONTH_DAY_CONDENSED));
		buttons.add(close);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(title, BorderLayout.NORTH);
		panel.add(backupTime, BorderLayout.CENTER);
		panel.add(buttons, BorderLayout.SOUTH);

		frame.add(panel);
		frame.setIconImage(Icons.APP_ICON.getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JRootPane rp = SwingUtilities.getRootPane(close);
		rp.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		rp.setDefaultButton(close);
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);

		close.addActionListener(e -> frame.dispose());

		backup.addActionListener(e -> {
			frame.dispose();
			performBackup(BACKUP_DIR);
		});
	}

	private String getLastBackupTime(SimpleDateFormat returnFormat) {
		File[] array = new File(BACKUP_DIR).listFiles();
		long lastBackupDate = 0;

		if (array == null || array.length == 0) {
			return "Never Backed Up";
		}

		for (File x : array) {
			long temp = x.lastModified();
			if (temp > lastBackupDate) {
				lastBackupDate = temp;
			}
		}
		return returnFormat.format(lastBackupDate);
	}

	private void performBackup(String backupDir) {
		String todaysDir = "backup_" + getToday();
		File fullDir = new File(backupDir + ApplicationLiterals.SLASH
				+ todaysDir);
		fullDir.mkdirs();

		try {
			String financialBackupScript = generateBackupCommand(fullDir,
					Databases.FINANCIAL);
			String accountsBackupScript = generateBackupCommand(fullDir,
					Databases.ACCOUNTS);

			Runtime.getRuntime().exec("cmd /c " + financialBackupScript);
			Runtime.getRuntime().exec("cmd /c " + accountsBackupScript);

			int olderBackups = deleteOtherBackups();

			String msg = "Successfully backed up databases!";
			if (olderBackups > 0) {
				msg = msg + "\nIncluding removing " + olderBackups
						+ " older backups";
			}

			JOptionPane.showMessageDialog(null, msg, "Success",
					JOptionPane.INFORMATION_MESSAGE);
			new Backup();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

	private String generateBackupCommand(File backupDir, Databases database)
			throws GeneralSecurityException, IOException {
		String port = ReadConfig.getConfigValue(ApplicationLiterals.DB_PORT);
		return "\"" + MYSQL_DIR + "mysqldump.exe\" -e -uroot " + "-p"
				+ Encoding.decrypt(ApplicationLiterals.getRootPassword())
				+ " -hlocalhost " + "-P" + port + ApplicationLiterals.SPACE
				+ database + " > " + backupDir + "\\" + database + ".sql";
	}

	private boolean enableBackup(SimpleDateFormat format) {
		String lastBackup = getLastBackupTime(format);

		return (!getToday().equals(lastBackup));
	}

	private String getToday() {
		return ApplicationLiterals.YEAR_MONTH_DAY_CONDENSED.format(new Date());
	}

	private int deleteOtherBackups() {
		File[] files = new File(BACKUP_DIR).listFiles();

		int deletedBackups = 0;
		for (File f : files) {
			if (!f.getName().contains(getToday())) {
				if (DeleteDirectory.deleteDir(f)) {
					deletedBackups++;
				}
			}
		}
		return deletedBackups;
	}
}

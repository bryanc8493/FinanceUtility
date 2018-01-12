package com.bryan.finance.gui.util;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;

public class Loading {

	private static JFrame frame;

	public Loading() {
		frame = new JFrame(ApplicationLiterals.APP_TITLE);
		JProgressBar pb = new JProgressBar();
		pb.setIndeterminate(true);

		JLabel title = new Title("Loading and initializing application....");

		JPanel content = new JPanel(new BorderLayout(0, 5));
		content.add(title, BorderLayout.NORTH);
		content.add(pb, BorderLayout.SOUTH);

		frame.add(content);
		frame.setIconImage(Icons.APP_ICON.getImage());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setResizable(false);
		JRootPane rp = SwingUtilities.getRootPane(frame);
		rp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}

	public static void terminate() {
		frame.dispose();
	}
}

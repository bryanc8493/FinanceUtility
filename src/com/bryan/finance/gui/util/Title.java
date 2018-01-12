package com.bryan.finance.gui.util;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.bryan.finance.literals.ApplicationLiterals;

public class Title extends JLabel {

	private static final long serialVersionUID = 1L;

	public Title(String s) {
		super(s, JLabel.CENTER);
		setFont(new Font("sans serif", Font.BOLD, 18));
		setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		setForeground(ApplicationLiterals.APP_COLOR);
	}
}

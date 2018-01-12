package com.bryan.finance.utilities;

import java.awt.Cursor;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class MultiLabelButton extends JButton {

	private static final long serialVersionUID = -7572817237399921788L;
	public static final int TOP = 1;
	public static final int BOTTOM = 2;

	public MultiLabelButton(String topLabel, String bottomLabel, ImageIcon icon) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JCenterLabel(topLabel));
		add(new JCenterLabel(icon));
		add(new JCenterLabel(bottomLabel));
		setCursor(new Cursor(Cursor.HAND_CURSOR));
	}

	public MultiLabelButton(String label, int position, ImageIcon icon) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		if (position == TOP) {
			add(new JCenterLabel(label));
			add(new JCenterLabel(icon));
		} else if (position == BOTTOM) {
			add(new JCenterLabel(icon));
			add(new JCenterLabel(label));
		}
		setCursor(new Cursor(Cursor.HAND_CURSOR));
	}
}

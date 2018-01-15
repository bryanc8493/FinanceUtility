package com.bryan.finance.utilities;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JPasswordField;

public class HintPassField extends JPasswordField {

	private static final long serialVersionUID = 1372062886713071090L;

	public HintPassField(String hint) {
		_hint = hint;
		this.setEchoChar('*');
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (new String(getPassword()).length() == 0) {
			int h = getHeight();
			((Graphics2D) g).setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			Insets ins = getInsets();
			FontMetrics fm = g.getFontMetrics();
			int c0 = getBackground().getRGB();
			int c1 = getForeground().getRGB();
			int m = 0xfefefefe;
			int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
			g.setColor(new Color(c2, true));
			g.drawString(_hint, ins.left, h / 2 + fm.getAscent() / 2 - 2);
		}
	}

	private final String _hint;
}

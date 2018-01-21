package com.bryan.finance.utilities;

import com.bryan.finance.literals.ApplicationLiterals;

import java.awt.*;

import javax.swing.JPasswordField;

public class HintPassField extends JPasswordField {

	private static final long serialVersionUID = 1372062886713071090L;

	public HintPassField(String hint, boolean isBig) {
		_hint = hint;
		this.setEchoChar('•');
		if(isBig) {
			this.setFont(ApplicationLiterals.APP_FONT);
			Dimension d = this.getPreferredSize();
			d.height = d.height + 10;
			this.setPreferredSize(d);
		}
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

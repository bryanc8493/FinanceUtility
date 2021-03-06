package com.bryan.finance.gui.util;

import javax.swing.JComponent;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class RequestFocusListener implements AncestorListener {
	private boolean removeListener;

	public RequestFocusListener() {
		this(true);
	}

	private RequestFocusListener(boolean removeListener) {
		this.removeListener = removeListener;
	}

	@Override
	public void ancestorAdded(AncestorEvent e) {
		JComponent component = e.getComponent();
		component.requestFocusInWindow();

		if (removeListener)
			component.removeAncestorListener(this);
	}

	@Override
	public void ancestorMoved(AncestorEvent e) {
	}

	@Override
	public void ancestorRemoved(AncestorEvent e) {
	}
}
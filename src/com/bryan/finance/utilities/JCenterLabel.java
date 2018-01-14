package com.bryan.finance.utilities;

import java.awt.Component;
import javax.swing.*;

public class JCenterLabel extends JLabel {

	private static final long serialVersionUID = 5502066664726732298L;

    public JCenterLabel(final String s)
    {
        super(s);
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public JCenterLabel(final Icon i)
    {
        super(i);
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}

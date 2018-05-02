package com.bryan.finance.gui.util;

import com.bryan.finance.literals.ApplicationLiterals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TabButton extends JButton{

    public TabButton(String label) {
        this.setText(label);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.setFont(ApplicationLiterals.TAB_FONT);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.setBorder(ApplicationLiterals.TAB_BORDER);
        this.setForeground(ApplicationLiterals.GREY_TAB);

        TabButton tab = this;

        this.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent me) {
                if (!tab.isActive()) {
                    tab.setForeground(Color.DARK_GRAY);
                }
            }
            public void mouseExited(MouseEvent me) {
                if (!tab.isActive()) {
                    tab.setForeground(ApplicationLiterals.GREY_TAB);
                }
            }
        });
    }

    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

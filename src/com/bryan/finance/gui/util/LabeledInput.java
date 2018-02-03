package com.bryan.finance.gui.util;

import com.bryan.finance.utilities.HintTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class LabeledInput extends JPanel {

    private JLabel top = new JLabel();
    private JTextField input;

    public LabeledInput(String label) {
        top.setText(label);
        input = new HintTextField(label, false);
        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(input, BorderLayout.SOUTH);

        top.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                String text = textField.getText();
                System.out.println(text);
                //textField.setText(text.toUpperCase());
            }
        });
    }

    public String getText() {
        return input.getText();
    }
}

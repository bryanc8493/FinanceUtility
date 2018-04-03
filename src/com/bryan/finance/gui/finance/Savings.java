package com.bryan.finance.gui.finance;

import com.bryan.finance.config.ReadConfig;
import com.bryan.finance.database.queries.Balance;
import com.bryan.finance.gui.util.PrimaryButton;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class Savings {

    private JFrame frame;

    private JLabel totalLbl = new JLabel("Total Amount");
    private JLabel safetyLbl = new JLabel("Emergency");
    private JLabel houseLbl = new JLabel("House");
    private JLabel weddingLbl = new JLabel("Wedding");

    private JLabel total = new JLabel();
    private JLabel safety = new JLabel();
    private JLabel house = new JLabel();
    private JLabel wedding = new JLabel();

    private Locale locale = new Locale("en", "US");
    private NumberFormat currencyFormatter = NumberFormat
            .getCurrencyInstance(locale);

    private final Logger logger = Logger.getLogger(Savings.class);

    public Savings() {
        logger.debug("Displaying Savings account data");
        frame = new JFrame("Savings Summary");

        setAmounts();
        alignRight();
        setFont();

        JLabel title = new Title("Savings Account Details");
        JButton close = new PrimaryButton("Close");

        JPanel content = new JPanel(new GridLayout(4,2, 0, 10));
        content.add(totalLbl); content.add(total);
        content.add(safetyLbl); content.add(safety);
        content.add(houseLbl); content.add(house);
        content.add(weddingLbl); content.add(wedding);
        content.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));

        JPanel button = new JPanel(new FlowLayout(FlowLayout.CENTER));
        button.add(close);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(title, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setIconImage(Icons.APP_ICON.getImage());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JRootPane rp = SwingUtilities.getRootPane(close);
        rp.setBorder(BorderFactory.createEmptyBorder(5,15,5,15));
        rp.setDefaultButton(close);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        close.addActionListener((e) -> frame.dispose());
    }

    private void setAmounts() {
        final Double HOUSE_AMT = Balance.getHouseSavings();

        double totalSavings = Balance.getSavingsBalance();
        String safetyString = ReadConfig.getConfigValue(ApplicationLiterals.SAVINGS_SAFE_AMT);
        double safetyAmt = Double.parseDouble(safetyString);
        double remainder = totalSavings - safetyAmt - HOUSE_AMT;

        total.setText(currencyFormatter.format(totalSavings));
        safety.setText(currencyFormatter.format(safetyAmt));
        house.setText(currencyFormatter.format(HOUSE_AMT));
        wedding.setText(currencyFormatter.format(remainder));

        safety.setForeground(Color.RED);
        house.setForeground(Color.RED);
    }

    private void alignRight() {
        total.setHorizontalAlignment(JLabel.RIGHT);
        safety.setHorizontalAlignment(JLabel.RIGHT);
        house.setHorizontalAlignment(JLabel.RIGHT);
        wedding.setHorizontalAlignment(JLabel.RIGHT);
    }

    private Set<JLabel> getLabelList() {
        Set<JLabel> labels = new HashSet<>();
        labels.add(total);
        labels.add(totalLbl);
        labels.add(safety);
        labels.add(safetyLbl);
        labels.add(house);
        labels.add(houseLbl);
        labels.add(wedding);
        labels.add(weddingLbl);
        return labels;
    }

    private void setFont() {
        final Font font = new Font("sans serif", Font.BOLD, 16);
        for (JLabel l : getLabelList()) {
            l.setFont(font);
        }
    }
}

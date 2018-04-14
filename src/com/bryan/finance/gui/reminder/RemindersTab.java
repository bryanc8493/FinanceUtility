package com.bryan.finance.gui.reminder;

import com.bryan.finance.database.Connect;
import com.bryan.finance.database.queries.QueryUtil;
import com.bryan.finance.gui.util.ApplicationControl;
import com.bryan.finance.gui.util.Title;
import com.bryan.finance.literals.ApplicationLiterals;
import com.bryan.finance.literals.Icons;
import com.bryan.finance.utilities.MultiLabelButton;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;

public class RemindersTab extends JPanel {

    private static Logger log = Logger.getLogger(RemindersTab.class);

    public RemindersTab() {
        Connection con = Connect.getConnection();
        log.debug("getting all data for reminders");

        final JButton add = new MultiLabelButton(" New Reminder ",
                MultiLabelButton.BOTTOM, Icons.ADD_ICON);
        final JButton edit = new MultiLabelButton(" Edit Reminders ",
                MultiLabelButton.BOTTOM, Icons.EDIT_ICON);

        JLabel title = new Title("Reminders");

        JLabel noReminders = new JLabel("You currently have no reminders!", JLabel.CENTER);
        noReminders.setFont(ApplicationLiterals.APP_FONT);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(add);
        buttons.add(edit);
        buttons.setBorder(BorderFactory.createEmptyBorder(0,0,50,0));

        JPanel content = new JPanel(new BorderLayout());
        content.add(buttons, BorderLayout.NORTH);
        if (QueryUtil.getTotalActiveReminders() == 0) {
            content.add(noReminders, BorderLayout.CENTER);
        } else {
            content.add(getReminderData(), BorderLayout.CENTER);
        }

        content.setBorder(ApplicationLiterals.PADDED_SPACE);

        this.setLayout(new BorderLayout(10, 10));
        this.add(title, BorderLayout.NORTH);
        this.add(content, BorderLayout.CENTER);
        this.add(
                ApplicationControl.closeAndLogout(con,
                        (JFrame) SwingUtilities.getRoot(this)),
                BorderLayout.SOUTH);

        add.addActionListener(e -> InsertReminder.addNewReminder());
        edit.addActionListener(e -> new ModifyReminders(false));
    }

    private static JScrollPane getReminderData() {
        Object[][] records = QueryUtil.getReminders();
        Object[] columnNames = { "Reminder Text", "Reminder Date" };

        DefaultTableModel model = new DefaultTableModel(records, columnNames);
        final JTable table = new JTable(model);
        final JScrollPane remSP = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        remSP.setViewportView(table);
        remSP.setVisible(true);
        Dimension d = table.getPreferredSize();
        remSP.setPreferredSize(new Dimension(d.width,
                table.getRowHeight() * 12));

        return remSP;
    }
}

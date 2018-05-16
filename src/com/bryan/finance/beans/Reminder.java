package com.bryan.finance.beans;

import java.util.Date;

public class Reminder {

    private String text;
    private Date date;
    private boolean isDismissed;
    private String dismissed;
    private String id;
    private String notes;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isDismissed() {
        return isDismissed;
    }

    public void setIsDismissed(boolean dismissed) {
        this.isDismissed = dismissed;
        setDismissed(dismissed);
    }

    public String getDismissed() {
        return dismissed;
    }

    private void setDismissed(boolean d) {
        if (d) {
            this.dismissed = "T";
        } else {
            this.dismissed = "F";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

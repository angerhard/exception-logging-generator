package com.andreas_gerhard.exceptgen.vo;

public class Text {

    public Text(String locale, String text) {
        this.locale = locale;
        this.text = text;
    }

    private String locale;
    private String text;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

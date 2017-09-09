package com.andreasgerhard.exceptgen.vo;

import java.util.ArrayList;
import java.util.List;

public class Entry {

    private List<Parameter> backendParameters = new ArrayList<>();
    private String backendText;
    private String domain;
    private String name;
    private Exception exception;

    public List<Parameter> getBackendParameters() {
        return backendParameters;
    }

    public void setBackendParameters(List<Parameter> backendParameters) {
        this.backendParameters = backendParameters;
    }

    public String getBackendText() {
        return backendText;
    }

    public void setBackendText(String backendText) {
        this.backendText = backendText;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

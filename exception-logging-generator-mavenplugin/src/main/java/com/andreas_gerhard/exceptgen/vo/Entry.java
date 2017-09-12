package com.andreas_gerhard.exceptgen.vo;

import com.andreas_gerhard.exceptgen.ParameterUtil;

import java.util.ArrayList;
import java.util.List;

public class Entry {

    private List<Parameter> backendParameters = new ArrayList<>();
    private String backendText;
    private String domain;
    private String name;
    private String properties;
    private com.andreas_gerhard.exceptgen.vo.Exception exception;

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

    public com.andreas_gerhard.exceptgen.vo.Exception getException() {
        return exception;
    }

    public void setException(com.andreas_gerhard.exceptgen.vo.Exception exception) {
        this.exception = exception;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getParameterString() {
        return ParameterUtil.getParameterString(getBackendParameters());
    }

    public String getParameterStringAppendix() {
        return ParameterUtil.getParameterString(getBackendParameters()) + (!getBackendParameters().isEmpty() ? "," : "");
    }
}

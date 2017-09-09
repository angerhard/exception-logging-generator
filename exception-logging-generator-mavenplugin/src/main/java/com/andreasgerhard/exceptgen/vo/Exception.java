package com.andreasgerhard.exceptgen.vo;

import java.util.ArrayList;
import java.util.List;

public class Exception {

    private String packageName;
    private String fqClassName;
    private String fqClassNameInherit;
    private List<Parameter> frontEndParameters = new ArrayList<>();
    private List<Text> frontEndText = new ArrayList<>();

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getFqClassName() {
        return fqClassName;
    }

    public void setFqClassName(String fqClassName) {
        this.fqClassName = fqClassName;
    }

    public String getFqClassNameInherit() {
        return fqClassNameInherit;
    }

    public void setFqClassNameInherit(String fqClassNameInherit) {
        this.fqClassNameInherit = fqClassNameInherit;
    }

    public List<Parameter> getFrontEndParameters() {
        return frontEndParameters;
    }

    public void setFrontEndParameters(List<Parameter> frontEndParameters) {
        this.frontEndParameters = frontEndParameters;
    }

    public List<Text> getFrontEndText() {
        return frontEndText;
    }

    public void setFrontEndText(List<Text> frontEndText) {
        this.frontEndText = frontEndText;
    }
}

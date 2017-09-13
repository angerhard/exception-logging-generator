package com.andreas_gerhard.exceptgen.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.andreas_gerhard.exceptgen.ParameterUtil;

public class Exception {

    private String packageName;
    private String fqClassName;
    private String className;
    private String fqClassNameInherit;
    private List<Parameter> frontEndParameters = new ArrayList<>();
    private List<Text> frontEndText = new ArrayList<>();
    private Integer returnCode;

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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public String getParameterString() {
        return ParameterUtil.getParameterString(getFrontEndParameters());
    }

    public String getParameterStringAppendix() {
        return ParameterUtil.getParameterString(getFrontEndParameters()) + (!getFrontEndParameters().isEmpty() ? "," : "");
    }


    public String getLocaleStr() {
        List<Text> frontEndText = getFrontEndText();
        List<String> locales = new ArrayList<>();
        for (Text text : frontEndText) {
            locales.add("\"" + text.getLocale() + "\"");
        }
        return StringUtils.join(locales, ", ");
    }

}

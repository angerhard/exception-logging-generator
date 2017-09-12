package com.andreas_gerhard.exceptgen.vo;

import com.andreas_gerhard.exceptgen.ParameterUtil;
import org.apache.commons.lang.StringUtils;

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

    public String getParameterString(){
        return ParameterUtil.getParameterString(getFrontEndParameters());
    }

    public String getParameterStringAppendix(){
        return ParameterUtil.getParameterString(getFrontEndParameters())+(!getFrontEndParameters().isEmpty() ? "," : "");
    }

    public String getLocaleStr() {
        List<Text> frontEndText = getFrontEndText();
        List<String> locales = new ArrayList<>();
        for (Text text : frontEndText) {
            locales.add("\""+text.getLocale()+"\"");
        }
        return StringUtils.join(locales, ", ");
    }

}

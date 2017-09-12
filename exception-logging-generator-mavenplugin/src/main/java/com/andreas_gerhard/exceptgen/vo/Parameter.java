package com.andreas_gerhard.exceptgen.vo;

import java.util.Objects;

public class Parameter implements Comparable<Parameter> {
    private String name;
    private String fq;
    private String tag;
    private boolean ignoreI18n;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFq() {
        return fq;
    }

    public void setFq(String fq) {
        this.fq = fq;
    }

    public boolean isIgnoreI18n() {
        return ignoreI18n;
    }

    public void setIgnoreI18n(boolean ignoreI18n) {
        this.ignoreI18n = ignoreI18n;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFieldDeclatation() {
        return String.format("private %s %s;", getFq(), getName());
    }

    public String getParameterToField() {
        return String.format("this.%s = %s;", getName(), getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return Objects.equals(name, parameter.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(Parameter parameter) {
        return parameter == null
                ||  parameter.getName() == null
                ? 1
                : parameter.getName().compareTo(getName() == null ? "" : getName())*-1;
    }
}

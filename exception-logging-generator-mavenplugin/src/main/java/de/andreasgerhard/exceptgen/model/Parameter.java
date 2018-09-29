package de.andreasgerhard.exceptgen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Parameter implements Comparable<Parameter> {
    private String name;
    private String fq;
    private String tag;
    private boolean ignoreI18n;

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
                || parameter.getName() == null
                ? 1
                : parameter.getName().compareTo(getName() == null ? "" : getName()) * -1;
    }
}

package de.andreasgerhard.exceptgen.model;

import de.andreasgerhard.exceptgen.ParameterUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Entry {

    private List<Parameter> backendParameters = new ArrayList<>();
    private String backendText;
    private String domain;
    private String name;
    private String properties;
    private Exception exception;

    public void updateException(Exception exception) {
        this.exception = exception;
    }

    public String getParameterString() {
        return ParameterUtil.getParameterString(getBackendParameters());
    }

    public String getParameterStringAppendix() {
        return ParameterUtil.getParameterString(getBackendParameters()) + (!getBackendParameters().isEmpty() ? "," : "");
    }
}

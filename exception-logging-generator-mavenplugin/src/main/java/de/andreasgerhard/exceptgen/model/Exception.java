package de.andreasgerhard.exceptgen.model;

import de.andreasgerhard.exceptgen.ParameterUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Exception {

    private String packageName;
    private String fqClassName;
    private String className;
    private String fqClassNameInherit;
    private List<Parameter> frontEndParameters = new ArrayList<>();
    private List<Text> frontEndText = new ArrayList<>();
    private Integer returnCode;

    public String getParameterString() {
        return ParameterUtil.getParameterString(getFrontEndParameters());
    }

    public String getParameterStringAppendix() {
        return ParameterUtil.getParameterString(getFrontEndParameters()) + (!getFrontEndParameters().isEmpty() ? "," : "");
    }

    public void addFrontEndText(Text text) {
        frontEndText.add(text);
    }

    public String getLocaleStr() {
        List<Text> frontEndText = getFrontEndText();
        List<String> locales = new ArrayList<>();
        for (Text text : frontEndText) {
            locales.add("\"" + text.getLocale() + "\"");
        }
        return StringUtils.join(locales, ", ");
    }

    public void updateFqClassNameInherit(String fqClassNameInherit) {


    }
}

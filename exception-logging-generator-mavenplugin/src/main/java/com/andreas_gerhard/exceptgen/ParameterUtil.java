package com.andreas_gerhard.exceptgen;

import com.andreas_gerhard.exceptgen.vo.Parameter;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ParameterUtil {

    public static String getParameterString(List<Parameter> parameters) {
        List<String> paraStr = new ArrayList<>();
        for (Parameter parameter : parameters) {
            paraStr.add(String.format("%s %s", parameter.getFq(), parameter.getName()));
        }
        return StringUtils.join(paraStr, ", ");
    }

}

package com.cvberry.berrypim;

import java.util.Map;

/**
 * Created by vancan1ty on 1/3/2016.
 */
public class TemplateEngine {
    public static String template(String templateStr, Map<String,String> stuff) {
        for (Map.Entry<String, String> entry : stuff.entrySet()) {
            templateStr = templateStr.replaceAll("\\{\\{"+entry.getKey()+"\\}\\}",entry.getValue());
        }
        return templateStr;
    }
}

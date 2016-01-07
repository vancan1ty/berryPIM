package com.cvberry.berrypim;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String templateWController(String templateStr, ControllerObject toUse, String[] pathComponents,
                                             Map<String, String[]> queryStr, String dataBody) {

        StringBuilder errorBuffer = new StringBuilder();
        StringBuffer theBuf = new StringBuffer();
        Pattern includePat = Pattern.compile("\\{\\{([^}]*)\\}\\}");
        Matcher m = includePat.matcher(templateStr);
        while(m.find()) {
            String templateName = m.group(1);
            String methodName = "fill_"+templateName;
            String iTemplateStr = "";
            try {
                Method toCall = toUse.getClass().getMethod(methodName, String[].class, Map.class, String.class);
                iTemplateStr = (String) toCall.invoke(toUse,pathComponents,queryStr,dataBody);

            } catch (NoSuchMethodException |InvocationTargetException | IllegalAccessException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                errorBuffer.append(sw.toString()+"\n");
                iTemplateStr = "could not template " + templateName;
                e.printStackTrace();
            }

            m.appendReplacement(theBuf,Matcher.quoteReplacement(iTemplateStr));
        }
        m.appendTail(theBuf);

        if(!errorBuffer.toString().isEmpty()) {
            theBuf.append("<div class=\"errors\">"+errorBuffer.toString()+"</div>");
        }

        return theBuf.toString();
    }

}

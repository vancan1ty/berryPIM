package com.cvberry.berrypim;

import com.cvberry.util.Utility;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vancan1ty on 1/3/2016.
 *
 * performs templating options using simplified {{var}} template syntax.
 */
public class Templater {

    String rootPath;
    public Map<String,String> registeredTemplates;

    public Templater(String rootPath) {
       this.rootPath = rootPath;
        this.registeredTemplates = new HashMap<>();
    }

    public void registerTemplates(List<String> filePaths) throws IOException {
        for (String path : filePaths) {
            File file = new File(path);
            String name = file.getName();

            ClassLoader classLoader = Templater.class.getClassLoader();
            InputStream theFlow = classLoader.getResourceAsStream(path);
            String templateStr = Utility.convertStreamToString(theFlow);

            registeredTemplates.put(name,templateStr);
        }
    }

    public String template(String templateStr) {
        String step1 = templateStr.replaceAll("\\{\\{rootPath\\}\\}",rootPath);
        String step2 = step1.replaceAll("\\{\\{appName\\}\\}",Anchor.getInstance().getAppName());
        StringBuffer theBuf = new StringBuffer();
        Pattern includePat = Pattern.compile("\\{\\{@include=\"([^}]*)\"\\}\\}");
        Matcher m = includePat.matcher(step2);
        while(m.find()) {
            String templateName = m.group(1);
            String iTemplateStr = registeredTemplates.get(templateName);
            m.appendReplacement(theBuf,iTemplateStr);
        }
        m.appendTail(theBuf);
        return theBuf.toString();
    }

    public String getMainTemplateContents() {
        return registeredTemplates.get("maintemplate.html");
    }

}

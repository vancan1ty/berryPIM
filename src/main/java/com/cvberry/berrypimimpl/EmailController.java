package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.ControllerObject;
import com.cvberry.berrypim.TemplateEngine;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;
import com.sun.management.OperatingSystemMXBean;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class EmailController extends PIMDefaultController implements ControllerObject {

    public EmailController(String controllerBase) {
        this.controllerBase = controllerBase;
    }

    @Override
    public List<Map.Entry<String, String>> getTopTabsItems() {
        String[] starter = {
                "gatech", "gatech",
                "gmail", "gmail",
        };
        return Utility.tupleizeArray(starter);
    }

    @Override
    public String fill_contentPane(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                   AuthInfoHolder authInfo) {
        StringBuilder out = new StringBuilder();
        String iframeTemplate = mTemplater.getTemplateContents("iframetemplate.html");
        String tab = Utility.getPathComponentOrDefault(pathComponents,1,"gatech");
        String url = null;
        if (tab.equals("gmail")) {
           url = "https://mail.google.com/mail/u/0/h/19fvjcysa35zu/";
        } else if (tab.equals("gatech")) {
            url = "http://mail.gatech.edu";
        } else {
            throw new RuntimeException("unknown tab!");
        }
        Map<String,String> nMap = new HashMap<>();
        nMap.put("iframe_url",url);
        out.append(TemplateEngine.template(iframeTemplate,nMap));
        return out.toString();
    }
}

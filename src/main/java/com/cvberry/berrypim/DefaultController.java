package com.cvberry.berrypim;

import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/3/2016.
 */
public class DefaultController implements ControllerObject {

    Anchor myAnchor;
    public String rootPathStr;
    public String controllerBase = "";
    public Templater mTemplater;

    public DefaultController() {
        myAnchor = Anchor.getInstance();
        rootPathStr = myAnchor.getRootPath() + "/";
        mTemplater = myAnchor.getTemplater();
    }

    public String control(String[] pathComponents, Map<String, String[]> queryParams, String template, String dataBody,
                          AuthInfoHolder authInfo) {
        String out = null;
        String restStr = Utility.getFirstQParamResult(queryParams, "rest");
        if (restStr != null) { //then bypass templating, treat this as an api request.

            String actionStr = Utility.getFirstQParamResult(queryParams, "action");
            if (actionStr == null) {
                throw new RuntimeException("no action specified.");
            }

            String methodName = "api_" + actionStr;
            try {
                Method toCall = this.getClass().getMethod(methodName, String[].class, Map.class, String.class,
                        AuthInfoHolder.class);
                out = (String) toCall.invoke(this, pathComponents, queryParams, dataBody, authInfo);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            out = TemplateEngine.templateWController(template, this, pathComponents, queryParams, dataBody, authInfo);
        }
        return out;
    }

    public String fill_ltabsItems(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                  AuthInfoHolder authInfo) {
        String selectorPath = (pathComponents[0] == null || pathComponents[0].isEmpty()) ?
                getDefaultTab() : pathComponents[0];
        List<Map.Entry<String, String>> items = getLTabItems();
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, String> entry : items) {
            String key = entry.getKey();
            String pathPart = key.split("\\?")[0];
            if (pathPart.toLowerCase().equals(selectorPath.toLowerCase())) {
                out.append("<li class='active'><a href='" + rootPathStr + entry.getKey() + "'>" + entry.getValue() + "</a></li>\n");
            } else {
                out.append("<li><a href='" + rootPathStr + entry.getKey() + "'>" + entry.getValue() + "</a></li>\n");
            }
        }
        return out.toString();
//        return "                    <li class=\"active\"><a href=\"raw\">Raw</a></li>\n" +
//                "                    <li><a href=\"dashboard\">Dashboard</a></li>\n" +
//                "                    <li><a href=\"addNew\">Data Entry</a></li>\n" +
//                "                    <li><a href=\"searchContacts\">Search Contacts</a></li>";
    }

    public String fill_topTabsItems(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                    AuthInfoHolder authInfo) {
        List<Map.Entry<String, String>> items = getTopTabsItems();
        String selectorPath = (pathComponents.length < 2 || pathComponents[1] == null || pathComponents[1].isEmpty()) ?
                items.get(0).getKey() : pathComponents[1];
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, String> entry : items) {
            String key = entry.getKey();
            if (key.toLowerCase().equals(selectorPath.toLowerCase())) {
                out.append("<li class='active'><a href='" + rootPathStr + controllerBase + entry.getKey() + "'>" + entry.getValue() + "</a></li>\n");
            } else {
                out.append("<li><a href='" + rootPathStr + controllerBase + entry.getKey() + "'>" + entry.getValue() + "</a></li>\n");
            }
        }
        return out.toString();

//        return "                    <li><a href=\"books.xml\">books.xml</a></li>\n" +
//                "                    <li class=\"active\"><a href=\"contacts.xml\">contacts.xml</a></li>\n" +
//                "                    <li><a href=\"contacts_schema.xml\">contacts_schema.xml</a></li>\n" +
//                "                    <li><a href=\"searchContacts\">Search Contacts</a></li>";

    }

    public String fill_contentPane(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                   AuthInfoHolder authInfo) throws Exception {
        return "I am content.";
    }

    public String fill_rightSideList(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                     AuthInfoHolder authInfo) throws Exception {
        return "";
    }

    public String api_echo(String[] pathComponents, Map<String, String[]> queryParams, String dataBody, AuthInfoHolder authInfo)
            throws Exception {
        return dataBody;
    }

    public List<Map.Entry<String, String>> getLTabItems() {
        String[] starter = {
                "raw", "Raw",
                "dashboard", "Dashboard",
                "addNew", "Data Entry",
                "searchContacts", "Search Contacts"
        };
        return Utility.tupleizeArray(starter);
    }

    public List<Map.Entry<String, String>> getTopTabsItems() {
        String[] starter = {
                "books.xml", "books.xml",
                "contacts.xml", "contacts.xml",
                "contacts_schema.xml", "contacts_schema.xml",
                "essaysdoc.org", "essaysdoc.org"
        };
        return Utility.tupleizeArray(starter);
    }

    public String getDefaultTab() {
        return "Raw";
    }

}

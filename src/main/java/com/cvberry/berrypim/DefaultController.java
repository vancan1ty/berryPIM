package com.cvberry.berrypim;

import com.cvberry.util.Utility;

import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/3/2016.
 */
public class DefaultController implements ControllerObject {

    Anchor myAnchor;
    String rootPathStr;
    public String controllerBase = "";

    public DefaultController() {
       myAnchor = Anchor.getInstance();
       rootPathStr = myAnchor.getRootPath()+"/";
    }

    public String control(String[] pathComponents, String queryStr, String template) {
        String out = TemplateEngine.templateWController(template, this, pathComponents, queryStr);
        return out;
    }

    public String fill_ltabsItems(String[] pathComponents, String queryStr) {
        String selectorPath = (pathComponents[0] == null || pathComponents[0].isEmpty()) ?
                getDefaultTab() : pathComponents[0];
        List<Map.Entry<String,String>> items = getLTabItems();
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String,String> entry : items) {
            String key = entry.getKey();
            if (key.toLowerCase().equals(selectorPath.toLowerCase())) {
                out.append("<li class='active'><a href='"+rootPathStr+entry.getKey()+"'>"+entry.getValue()+"</a></li>\n");
            } else {
                out.append("<li><a href='"+rootPathStr+entry.getKey()+"'>"+entry.getValue()+"</a></li>\n");
            }
        }
        return out.toString();
//        return "                    <li class=\"active\"><a href=\"raw\">Raw</a></li>\n" +
//                "                    <li><a href=\"dashboard\">Dashboard</a></li>\n" +
//                "                    <li><a href=\"addNew\">Data Entry</a></li>\n" +
//                "                    <li><a href=\"searchContacts\">Search Contacts</a></li>";
    }

    public String fill_topTabsItems(String[] pathComponents, String queryStr) {
        List<Map.Entry<String,String>> items = getTopTabsItems();
        String selectorPath = (pathComponents.length < 2 || pathComponents[1] == null || pathComponents[1].isEmpty()) ?
                items.get(0).getKey() : pathComponents[1];
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String,String> entry : items) {
            String key = entry.getKey();
            if (key.toLowerCase().equals(selectorPath.toLowerCase())) {
                out.append("<li class='active'><a href='"+rootPathStr+controllerBase+entry.getKey()+"'>"+entry.getValue()+"</a></li>\n");
            } else {
                out.append("<li><a href='"+rootPathStr+controllerBase+entry.getKey()+"'>"+entry.getValue()+"</a></li>\n");
            }
        }
        return out.toString();

//        return "                    <li><a href=\"books.xml\">books.xml</a></li>\n" +
//                "                    <li class=\"active\"><a href=\"contacts.xml\">contacts.xml</a></li>\n" +
//                "                    <li><a href=\"contacts_schema.xml\">contacts_schema.xml</a></li>\n" +
//                "                    <li><a href=\"searchContacts\">Search Contacts</a></li>";

    }

    public String fill_contentPane(String[] pathComponents, String queryStr) {
        return "I am content.";
    }

    public List<Map.Entry<String, String>> getLTabItems() {
        String[] starter = {
                "raw", "Raw",
                "dashboard", "Dashboard",
                "addNew", "Date Entry",
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

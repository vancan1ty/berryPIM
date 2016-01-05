package com.cvberry.berrypim;

/**
 * Created by vancan1ty on 1/3/2016.
 */
public class DefaultController implements ControllerObject {

    public String control(String[] pathComponents, String queryStr, String template) {
        String out = TemplateEngine.templateWController(template,this,pathComponents,queryStr);
        return out;
    }

    public String fill_ltabsItems(String[] pathComponents, String queryStr) {
        return  "                    <li class=\"active\"><a href=\"raw\">Raw</a></li>\n" +
                "                    <li><a href=\"dashboard\">Dashboard</a></li>\n" +
                "                    <li><a href=\"addNew\">Data Entry</a></li>\n" +
                "                    <li><a href=\"searchContacts\">Search Contacts</a></li>";
    }

    public String fill_topTabsItems(String[] pathComponents, String queryStr) {
        return  "                    <li><a href=\"books.xml\">books.xml</a></li>\n" +
                "                    <li class=\"active\"><a href=\"contacts.xml\">contacts.xml</a></li>\n" +
                "                    <li><a href=\"contacts_schema.xml\">contacts_schema.xml</a></li>\n" +
                "                    <li><a href=\"searchContacts\">Search Contacts</a></li>";

    }

    public String fill_contentPane(String[] pathComponents, String queryStr) {
        return  "I am content.";
    }

}

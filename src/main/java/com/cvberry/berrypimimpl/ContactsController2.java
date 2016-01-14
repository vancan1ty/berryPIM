package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.Anchor;
import com.cvberry.berrypim.ControllerObject;
import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class ContactsController2 extends PIMDefaultController implements ControllerObject {

    DataFilesManager filesManager;

    //CB TODO refactor constructors so that DefaultController gets controllerBase, none other need it.
    public ContactsController2(String controllerBase) throws IOException {
        this.controllerBase = controllerBase;
        filesManager = Anchor.getInstance().getDataFilesManager();
    }

    @Override
    public List<Map.Entry<String, String>> getTopTabsItems() {
        String[] starter = {
                "home", "Contacts 2 Home",
        };
        return Utility.tupleizeArray(starter);
    }

    @Override
    public String fill_contentPane(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                   AuthInfoHolder authInfo) throws Exception {
        String doctoredTemplate = Anchor.getInstance().getTemplater().getTemplateContents("doctoredTemplate.html");

        String rDoctoredTemplate = Anchor.getInstance().getTemplater().template(doctoredTemplate);
        StringBuilder out = new StringBuilder();
        String contactsStr = filesManager.getFileContents("contacts.xml");


//        DocumentBuilderFactory factory = Utility.getConfiguredDocBuilderFactory();
//
//        //Now use the factory to create a DOM parser, a.k.a. DocumentBuilder
//        DocumentBuilder parser = null;
//        parser = factory.newDocumentBuilder();
//
//        String xpathStr = "/contacts/contact";
//        Document document = null;
//        document = parser.parse(new InputSource(new StringReader(contactsStr)));
//
//        XPath xPath = XPathFactory.newInstance().newXPath();
//        NodeList nodeList = null;
//        nodeList = (NodeList) xPath.compile(xpathStr).evaluate(document, XPathConstants.NODESET);
//        String[] results = new String[nodeList.getLength()];
//        for (int index = 0; index < nodeList.getLength(); index++) {
//            Node node = nodeList.item(index);
//            String name = xPath.compile("name").evaluate(node);
//            results[index] = name;
//
//        }

//        out.append("<ul>");
//        for (String result : results) {
//            out.append("<li>"+result+"</li>");
//        }
//        out.append("</ul>");


        out.append(rDoctoredTemplate);

        return out.toString();
    }

}

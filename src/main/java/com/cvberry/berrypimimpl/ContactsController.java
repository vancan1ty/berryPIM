package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.Anchor;
import com.cvberry.berrypim.ControllerObject;
import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.util.Utility;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class ContactsController extends PIMDefaultController implements ControllerObject {

    DataFilesManager filesManager;

    //CB TODO refactor constructors so that DefaultController gets controllerBase, none other need it.
    public ContactsController(String controllerBase) {
        this.controllerBase = controllerBase;
        filesManager = Anchor.getInstance().getDataFilesManager();
    }

    public List<Map.Entry<String, String>> getTopTabsItems() {
        String[] starter = {
                "home", "Contacts Home",
        };
        return Utility.tupleizeArray(starter);
    }

    public String fill_contentPane(String[] pathComponents, String queryStr) throws Exception {
        StringBuilder out = new StringBuilder();
        String contactsStr = filesManager.getFileContents("contacts.xml");

        DocumentBuilderFactory factory = Utility.getConfiguredDocBuilderFactory();

        //Now use the factory to create a DOM parser, a.k.a. DocumentBuilder
        DocumentBuilder parser = null;
        parser = factory.newDocumentBuilder();

        String xpathStr = "/contacts/contact";
        Document document = null;
        document = parser.parse(new InputSource(new StringReader(contactsStr)));

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList nodeList = null;
        nodeList = (NodeList) xPath.compile(xpathStr).evaluate(document, XPathConstants.NODESET);
        String[] results = new String[nodeList.getLength()];
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            String name = xPath.compile("name").evaluate(node);
            results[index] = name;

        }

        out.append("<ul>");
        for (String result : results) {
            out.append("<li>"+result+"</li>");
        }
        out.append("</ul>");

        return out.toString();
    }

}

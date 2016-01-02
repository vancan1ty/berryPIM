package com.cvberry.berrypim;

import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.*;

import com.cvberry.berrypim.data.PhoneNumber;
import com.cvberry.berrypim.data.TypeAnnotatedString;
import org.w3c.dom.*;


/**
 * Created by vancan1ty on 12/3/2015.
 *
 * Based on DOM XML Parsing example from page 279 of "Java in a Nutshell, 5th Edition, David Flanagan"
 *
 */
public class DataLoader {

    public static void main(String[] args) throws IOException,
            ParserConfigurationException, org.xml.sax.SAXException {

        DocumentBuilderFactory factory = getConfiguredFactory();

        //Now use the factory to create a DOM parser, a.k.a. DocumentBuilder
        DocumentBuilder parser = factory.newDocumentBuilder();

        //Parse the file and build a Document tree to represent its content
        Document document = parser.parse(new File(args[0]));

        //Now populate each contact in turn
        Node firstChild = document.getFirstChild();
        String name;
        List<String> tags;
        LocalDate birthday;
        List<PhoneNumber> phoneNumbers;
        List<TypeAnnotatedString> emails;
        List<TypeAnnotatedString> addresses;
        String note;

        System.out.println(firstChild.getNodeName());
        NodeList contacts = firstChild.getChildNodes();
        for (int i = 0; i < contacts.getLength(); i++) {
            Node contact = contacts.item(i);
            NodeList contactKids = contact.getChildNodes();
            for (int i2 = 0; i2 < contactKids.getLength(); i2++) {
                Node n = contactKids.item(i2);
                switch(n.getLocalName()) {
                    case "name": {
                        name = n.getTextContent();
                        break;
                    }
                    case "tags": {
                        tags = Arrays.asList(n.getTextContent().split(" "));
                        break;
                    }
                    case "birthday": {
                        Local
                        break;
                    }
                }
            }

        }




    }

    public static DocumentBuilderFactory getConfiguredFactory() {
        //Create a factory object for creating DOM parsers and configure it.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true); //We want to ignore comments
        factory.setCoalescing(true); // Convert CDATA to Text nodes
        factory.setNamespaceAware(false); // No namespaces: this is default
        factory.setValidating(false); // Don't validate DTD: also default

        return factory;
    }

}

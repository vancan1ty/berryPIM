package com.cvberry.berrypim;

import com.cvberry.util.ResourceLister;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class Bootstrap {

    public static void bootstrap(String rootPath) throws IOException, SAXException, ParserConfigurationException,
            XPathExpressionException, ClassNotFoundException, IllegalAccessException, InstantiationException, URISyntaxException {
        DocumentBuilderFactory factory = getConfiguredFactory();

        //Now use the factory to create a DOM parser, a.k.a. DocumentBuilder
        DocumentBuilder parser = factory.newDocumentBuilder();

        //Parse the file and build a Document tree to represent its content
        InputStream configStream = Bootstrap.class.getResourceAsStream("/config.xml");
        Document document = parser.parse(configStream);

        String xpath = "/config/configurators";
        XPath xPath = XPathFactory.newInstance().newXPath();
        /*NodeList nodeList = (NodeList) xPath.compile(xpath).evaluate(xpath, XPathConstants.NODESET);
        String[] results = new String[nodeList.getLength()];
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            String name = node.getAttributes().getNamedItem("name").getTextContent();
            results[index] = name;
        }*/
        String rawOutput = xPath.compile(xpath).evaluate(document).trim();
        System.out.println("raw output");
        System.out.println(rawOutput);
        String[] classes = rawOutput.split("\\s+");
        System.out.println("Configurator classes to load: ");
        System.out.println(Arrays.stream(classes).collect(Collectors.joining(",")).toString());
        for (int i = 0; i < classes.length; i++) {
            Class configClass = Class.forName(classes[i]);
            Configurator configurator = (Configurator) configClass.newInstance();
            configurator.doConfiguration(rootPath);
        }
        //System.out.println(classes.length);
        //System.out.println("all resources.");
        //System.out.println(ResourceLister.listResources(Pattern.compile(".*\\.xml")));
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
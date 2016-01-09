package com.cvberry.berrypim;

import com.cvberry.util.Utility;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Created by vancan1ty on 1/4/2016.
 *
 * if 'mconfig.xml' is present on classpath, use that.
 * otherwise, use config.xml
 */
public class Bootstrap {

    public static void bootstrap(String rootPath) throws IllegalAccessException, ParserConfigurationException, IOException, XPathExpressionException, InstantiationException, URISyntaxException, SAXException, ClassNotFoundException {
        bootstrap(rootPath,null);
    }
    public static void bootstrap(String rootPath,String optionalFilesRoot) throws IOException, SAXException, ParserConfigurationException,
            XPathExpressionException, ClassNotFoundException, IllegalAccessException, InstantiationException, URISyntaxException {

        Anchor myAnchor = Anchor.getInstance();
        myAnchor.setRootPath(rootPath);

        String prospectivePimFilesRoot = System.getProperty("BERRYPIM_DATA_ROOT");
        if(optionalFilesRoot!=null) {
            myAnchor.setPIMFilesRoot(optionalFilesRoot);
        } else if (prospectivePimFilesRoot == null) {
            myAnchor.setPIMFilesRoot("berryData");
        } else {
            myAnchor.setPIMFilesRoot(prospectivePimFilesRoot);
        }

        ImageStreamer imStreamer = new ImageStreamer();
        myAnchor.setImageStreamer(imStreamer);

        DocumentBuilderFactory factory = Utility.getConfiguredDocBuilderFactory();

        //Now use the factory to create a DOM parser, a.k.a. DocumentBuilder
        DocumentBuilder parser = factory.newDocumentBuilder();

        //Parse the file and build a Document tree to represent its content
        InputStream configStream = ConfigXMLFileFinder.getConfigXMLStream();

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
        System.out.println(Utility.join(",",Arrays.asList(classes)));
        for (int i = 0; i < classes.length; i++) {
            Class configClass = Class.forName(classes[i]);
            Configurator configurator = (Configurator) configClass.newInstance();
            configurator.doConfiguration(rootPath);
        }

        String authRealm = xPath.compile("/config/authrealm").evaluate(document).trim();
        myAnchor.setAuthRealm(authRealm);
        //System.out.println(classes.length);
        //System.out.println("all resources.");
        //System.out.println(ResourceLister.listResources(Pattern.compile(".*\\.xml")));
    }


}

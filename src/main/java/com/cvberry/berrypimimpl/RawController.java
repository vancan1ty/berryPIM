package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.Anchor;
import com.cvberry.berrypim.ControllerObject;
import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.util.Utility;
import com.sun.management.OperatingSystemMXBean;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class RawController extends PIMDefaultController implements ControllerObject {

    Anchor myAnchor;
    DataFilesManager filesManager;

    public RawController(String controllerBase) {
        myAnchor = Anchor.getInstance();
        filesManager = myAnchor.getDataFilesManager();
        this.controllerBase = controllerBase;
    }

    public List<Map.Entry<String, String>> getTopTabsItems() {
        List<String> fileNames = filesManager.listDataFiles();
        String[] starter = new String[fileNames.size() * 2];
        for (int i = 0; i < starter.length - 1; i += 2) {
            starter[i] = fileNames.get(i / 2);
            starter[i + 1] = fileNames.get(i / 2);
        }

        return Utility.tupleizeArray(starter);
    }

    public String getFileName(String[] pathComponents) {
        return Utility.getPathComponentOrDefault(pathComponents, 1, getTopTabsItems().get(0).getKey());
    }

    public String getFunction(String[] pathComponents) {
        return Utility.getPathComponentOrDefault(pathComponents, 1, getTopTabsItems().get(0).getKey());
    }

    public String fill_contentPane(String[] pathComponents, String queryStr) throws SAXException, ParserConfigurationException, XPathExpressionException, IOException, TransformerException {
        StringBuilder out = new StringBuilder();
        String fileName = getFileName(pathComponents);

        String fileContents = filesManager.getFileContents(fileName);
        out.append("<textarea class='fullsize'>");
        out.append(fileContents);
        out.append("</textarea>");

        if (fileName.endsWith(".xml") || fileName.endsWith(".xsd")) {
            out.append("<b>Run XPath</b>\n");

            if (queryStr != null && queryStr.startsWith("xpath")) {
                String xpath = queryStr.substring(queryStr.indexOf("=") + 1);
                out.append("<form>\n");
                out.append("<input name='xpath' value='" + xpath + "'></input>");
                out.append("<input type='submit'></input>");
                out.append("</form>\n");
                out.append("results:<br>\n");
                out.append("<pre>\n");
                String results = Utility.runXPathOnString(fileContents, xpath);
                String escapedResults = Utility.escapeXML(results);
                out.append(escapedResults);
                out.append("</pre>");
            } else {
                out.append("<form>\n");
                out.append("<input name='xpath'></input>");
                out.append("<input type='submit'></input>");
                out.append("</form>\n");
            }
        }
        return out.toString();
    }
}

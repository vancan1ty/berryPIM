package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.Anchor;
import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.berrypim.PageComponent;
import com.cvberry.berrypim.Templater;
import com.cvberry.berrypim.widgets.PieChart;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;
import net.sf.saxon.s9api.*;
import org.jfree.data.general.PieDataset;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/18/2016.
 *
 * TODO add disable basic feature in "advanced" menu which sets cookie and enables client-side graphs.
 */
public class GraphsComponent implements PageComponent {

    public String fileName;
    public Templater templater;
    public DataFilesManager filesManager;
    public GraphsComponent(String fileName, Templater templater, DataFilesManager filesManager) {
       this.fileName = fileName;
        this.templater = templater;
        this.filesManager = filesManager;
    }

    @Override
    public String makeContentPaneHTML(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                      AuthInfoHolder authInfo) throws ParserConfigurationException, TransformerException, IOException, XPathExpressionException, SaxonApiException, SAXException, XPathFactoryConfigurationException {
        String xXPATH = Utility.getFirstQParamResult(queryParams,"x");
        String yXPATH = Utility.getFirstQParamResult(queryParams,"y");
        String categoryXPATH = Utility.getFirstQParamResult(queryParams,"category");
        String zXPATH = Utility.getFirstQParamResult(queryParams,"z");
        String graphType = Utility.getFirstQParamResult(queryParams,"gtype");
        StringBuilder out = new StringBuilder();
        String graphChooserContents = templater.getTemplateContents("graphChooserTemplate.html");
        out.append(graphChooserContents);
        String document = filesManager.getFileContents(fileName);
        if(graphType!=null && graphType.equals("pie") && yXPATH!=null && categoryXPATH !=null) {

            XdmSequenceIterator catResults = Utility.runXQueryOnStringToDS(document, categoryXPATH);
            XdmSequenceIterator amountResults = Utility.runXQueryOnStringToDS(document, yXPATH);
            if (catResults.hasNext() && amountResults.hasNext()) {
                List<String> categoriesL = new ArrayList<>();
                List<Double> amountsL = new ArrayList<>();
                while (catResults.hasNext() && amountResults.hasNext()) {
                    XdmItem cat = catResults.next();
                    XdmItem amount = amountResults.next();
                    categoriesL.add(cat.getStringValue());
                    amountsL.add(Double.parseDouble(amount.getStringValue()));
                }
                String[] categories = categoriesL.toArray(new String[0]);
                Double[] amounts = amountsL.toArray(new Double[0]);
                PieDataset myDSet = PieChart.createDatasetFromArrays(categories, amounts);
                String link = PieChart.createAndEnqueueChart(myDSet, "", Anchor.getInstance().getRootPath(), true, true, 300, 300);
                out.append(link);

            } else {
                out.append("<p>no results to show</p>");
            }
        } else {
            out.append("<p>no results to show</p>");
        }

        return out.toString();
    }


}

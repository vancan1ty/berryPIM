package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.*;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;
import net.sf.saxon.s9api.*;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYZDataset;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by vancan1ty on 1/18/2016.
 * <p>
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

    public List<String> getStringListResults(XdmSequenceIterator iterator) {
        List<String> out = new ArrayList<>();
        while (iterator.hasNext()) {
            XdmItem next = iterator.next();
            out.add(next.getStringValue());
        }
        return out;
    }

    public List<Double> getDoubleListResults(XdmSequenceIterator iterator) {
        List<Double> out = new ArrayList<>();
        while (iterator.hasNext()) {
            XdmItem next = iterator.next();
            out.add(Double.parseDouble(next.getStringValue()));
        }
        return out;
    }

    @Override
    public String makeContentPaneHTML(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                      AuthInfoHolder authInfo) throws ParserConfigurationException, TransformerException, IOException, XPathExpressionException, SaxonApiException, SAXException, XPathFactoryConfigurationException {
        String xXPATH = Utility.getFirstQParamResult(queryParams, "x");
        if(Utility.nullOrEmpty(xXPATH)) {xXPATH = "return";}
        String yXPATH = Utility.getFirstQParamResult(queryParams, "y");
        if(Utility.nullOrEmpty(yXPATH)) {yXPATH = "return";}
        String zXPATH = Utility.getFirstQParamResult(queryParams, "z");
        if(Utility.nullOrEmpty(zXPATH)) {zXPATH = "return";}
        String categoryXPATH = Utility.getFirstQParamResult(queryParams, "category");
        if(Utility.nullOrEmpty(categoryXPATH)) {categoryXPATH= "return";}
        String topCatXPATH = Utility.getFirstQParamResult(queryParams, "topcat");
        if(Utility.nullOrEmpty(topCatXPATH)) {topCatXPATH= "return";}

        StringBuilder out = new StringBuilder();
        String graphType = Utility.getFirstQParamResult(queryParams, "gtype");
        if(graphType==null) {graphType="pie";}
        String graphChooserContents = templater.getTemplateContents("graphChooserTemplate.html");
        out.append(graphChooserContents);

        String document = filesManager.getFileContents(fileName);
        XdmSequenceIterator xResults = Utility.runXQueryOnStringToDS(document,xXPATH);
        XdmSequenceIterator yResults = Utility.runXQueryOnStringToDS(document,yXPATH);
        XdmSequenceIterator zResults = Utility.runXQueryOnStringToDS(document,zXPATH);
        XdmSequenceIterator categoryResults = Utility.runXQueryOnStringToDS(document,categoryXPATH);
        XdmSequenceIterator topCatResults = Utility.runXQueryOnStringToDS(document,topCatXPATH);
        if (graphType.equals("pie")) {
            if (yResults.hasNext()) {
                List<String> categoriesL = getStringListResults(categoryResults);
                List<Double> amountsL = getDoubleListResults(yResults);
                PieDataset myDSet = ChartingUtil.createPieDataset(categoriesL, amountsL);
                String link = ChartingUtil.createAndEnqueuePieChart(myDSet, "", Anchor.getInstance().getRootPath(), true, true, 300, 300);
                out.append(link);
            } else {
                out.append("<p>no results to show</p>");
            }
        } else
        if (graphType.equals("bar")) {
            if (yResults.hasNext()) {
                List<String> categories = getStringListResults(categoryResults);
                List<Double> y = getDoubleListResults(yResults);
                List<String> topCats = getStringListResults(topCatResults);
                CategoryDataset dataset = ChartingUtil.createCategoryDataset(y,categories,topCats);
                String link = ChartingUtil.createAndEnqueueBarChart(dataset, "", Anchor.getInstance().getRootPath(), true, true, 500, 300);
                out.append(link);
            } else {
                out.append("<p>no results to show</p>");
            }
        } else
        if (graphType.equals("line")) {
            if (yResults.hasNext()) {
                List<String> categories = getStringListResults(categoryResults);
                List<Double> y = getDoubleListResults(yResults);
                List<String> topCats = getStringListResults(topCatResults);
                CategoryDataset dataset = ChartingUtil.createCategoryDataset(y,categories,topCats);
                String link = ChartingUtil.createAndEnqueueLineChart(dataset, "", Anchor.getInstance().getRootPath(), true, true, 500, 300);
                out.append(link);
            } else {
                out.append("<p>no results to show</p>");
            }
        } else
        if (graphType.equals("linexy")) {
            if (xResults.hasNext()) {
                List<String> categories = getStringListResults(categoryResults);
                List<Double> x = getDoubleListResults(xResults);
                List<Double> y = getDoubleListResults(yResults);
                XYZDataset dataset = ChartingUtil.createXYZDataset(x,y,null,categories);
                String link = ChartingUtil.createAndEnqueueXYLineChart(dataset, "", Anchor.getInstance().getRootPath(), true, true, 500, 300);
                out.append(link);
            } else {
                out.append("<p>no results to show</p>");
            }
        } else
        if (graphType.equals("scatter")) {
            if (xResults.hasNext()) {
                List<String> categories = getStringListResults(categoryResults);
                List<Double> x = getDoubleListResults(xResults);
                List<Double> y = getDoubleListResults(yResults);
                List<Double> z = getDoubleListResults(zResults);
                XYZDataset dataset = ChartingUtil.createXYZDataset(x,y,z,categories);
                String link = ChartingUtil.createAndEnqueueXYZScatterPlot(dataset, "", Anchor.getInstance().getRootPath(), true, true, 500, 300);
                out.append(link);
            } else {
                out.append("<p>no results to show</p>");
            }
        }

        else {
            out.append("<p>no results to show</p>");
        }

        return out.toString();
    }


}

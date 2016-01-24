package com.cvberry.berrypim.calendar;

import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.util.Utility;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmSequenceIterator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by vancan1ty on 1/19/2016.
 */
public class EffectiveCalendarGenerator {

    DataFilesManager filesManager;

    public EffectiveCalendarGenerator(DataFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    public Document getEffectiveCalendar() throws ParserConfigurationException, SAXException, IOException, XPathFactoryConfigurationException, SaxonApiException, XPathExpressionException, TransformerException {
        Document parsedCalendar = getParsedCalendar();

        XdmSequenceIterator repeatedEvents = Utility.runXQueryOnDOMDocument(parsedCalendar, "//vevent[properties/rrule]");
        int repEventID = 0;
        while (repeatedEvents.hasNext()) {
            XdmItem event = repeatedEvents.next();
            XdmSequenceIterator recurrences = Utility.runXQueryOnXdmItem(event,"/properties/rrule/recur");
            while(recurrences.hasNext()) {
                XdmItem recurrRule = recurrences.next();
                String freq = Utility.runXQueryOnXdmItemToString(recurrRule,"/freq/string()");
                int number = Integer.parseInt(Utility.runXQueryOnXdmItemToString(recurrRule,"/count/string()"));
                for (int i = 0; i < number; i++) {

                }
            }
        }


        return null;
    }

    public Document getParsedCalendar() throws IOException, SAXException, ParserConfigurationException {
        String calendarStr = filesManager.getFileContents("calendar.xml");
        InputStream asStream = new ByteArrayInputStream(calendarStr.getBytes("UTF-8"));
        Document parsedCal = parseFile(asStream);
        return parsedCal;
    }

    public Document parseFile(InputStream fileContents) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = Utility.getConfiguredDocBuilderFactory();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(fileContents);
        return doc;
    }

}

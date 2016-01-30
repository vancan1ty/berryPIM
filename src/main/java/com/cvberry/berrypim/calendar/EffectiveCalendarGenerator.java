package com.cvberry.berrypim.calendar;

import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.util.DomVisitingHelper;
import com.cvberry.util.Utility;
import net.sf.saxon.s9api.SaxonApiException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

import static com.cvberry.util.Utility.strOrNull;

/**
 * Created by vancan1ty on 1/19/2016.
 */
public class EffectiveCalendarGenerator {

    DataFilesManager filesManager;

    public EffectiveCalendarGenerator(DataFilesManager filesManager) {
        this.filesManager = filesManager;
    }

    /**
     * @param doc not modified, used as an instance for createElement method
     * @return node with filled out properties
     */
    public static Node generateEventNodeWithInfo(Document doc, SimpleEventFields fields) {
        if (fields.name == null || fields.dateTime == null) {
            throw new RuntimeException("no event name and/or date");
        }
        Node node = doc.createElement("vevent");
        Node propsNode = node.appendChild(doc.createElement("properties"));
        Node dateTime = propsNode.appendChild(doc.createElement("dtstart")).appendChild(doc.createElement("date-time"));
        String nowAsISO = Utility.isoDateFormatter.format(fields.dateTime);
        dateTime.appendChild(doc.createTextNode(nowAsISO));
        Node durationN = propsNode.appendChild(doc.createElement("duration")).appendChild(doc.createElement("duration"));
        String durationStr;
        if (fields.duration != null) {
            durationStr = fields.duration.toString();
        } else {
            durationStr = "PT1H"; //default to one hour
        }
        Node text = propsNode.appendChild(doc.createElement("summary")).appendChild(doc.createElement("text"));
        text.appendChild(doc.createTextNode(fields.name));
        durationN.appendChild(doc.createTextNode(durationStr));
        if (fields.description != null) {
            Node description = propsNode.appendChild(doc.createElement("description")).appendChild(doc.createElement("text"));
            description.appendChild(doc.createTextNode(fields.description));
        }
        return node;
    }

    public static SimpleEventFields getSimpleEventFieldsFromVEvent(DatatypeFactory dTypeFactory, Node event) {
        String eventName;
        Date rDate;
        Duration duration;
        String description;
        try {
            eventName = Utility.runXPathOnDOMNodeToString(event, "properties/summary/text/text()");
            if (eventName == null || eventName.trim().isEmpty()) {
                return null;
            }
            String eventDateStr = Utility.runXPathOnDOMNodeToString(event, "properties/dtstart/date-time/text()");
            if (eventDateStr == null || eventDateStr.trim().isEmpty()) {
                return null;
            }

            rDate = Utility.isoDateFormatter.parse(eventDateStr);
            String eventDurationStr = Utility.runXPathOnDOMNodeToString(event, "properties/duration/duration/text()");
            if (eventDurationStr != null && !eventDurationStr.trim().isEmpty()) {
                duration = dTypeFactory.newDuration(eventDurationStr);
            } else {
                duration = null;
            }
            description = Utility.runXPathOnDOMNodeToString(event, "properties/description/text/text()");
        } catch (Exception e) {
            throw new RuntimeException("at event " + event.getNodeName(), e);
        }
        SimpleEventFields simpleEvent = new SimpleEventFields(eventName, rDate, duration, description);
        return simpleEvent;

    }

    public static class ItemRecurrenceHandler extends DomVisitingHelper.ItemVisitorHelper {
        final Document doc;
        final DatatypeFactory dTypeFactory;
        final List<Node> elementsToAppendChildTo = new ArrayList<Node>();
        final List<Node> elementsToAppendChildBefore = new ArrayList<Node>();
        final List<Node> elementsToAppend = new ArrayList<Node>();

        public ItemRecurrenceHandler(Document doc) throws DatatypeConfigurationException {
            this.doc = doc;
            dTypeFactory = DatatypeFactory.newInstance();
        }

        @Override
        public Boolean apply(Node node) {
            if (node == null) {
                return false;
            }

            if (node.getNodeName() != null && node.getNodeName().equals("rrule")) {
                //rrule is always positioned like so
                //parent/properties/rrule
                Node event = node.getParentNode().getParentNode();
                if(!event.getNodeName().equals("vevent")) {
                    return false;
                }
                SimpleEventFields simpleEvent = getSimpleEventFieldsFromVEvent(dTypeFactory,event);
                if(simpleEvent == null) {
                    return false;
                }

                Node eventParent = event.getParentNode();
                Node recur = null;
                try {
                    recur = Utility.runXPathOnDOMNode(event,"properties/rrule/recur").item(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                Collection<Date> dates;
                try {
                    dates = EffectiveCalendarGenerator.generateDatesForRecurrencePattern(simpleEvent.dateTime, recur);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                for (Date d : dates) {
                    SimpleEventFields newFields = new SimpleEventFields(simpleEvent,d);
                    Node newNode = generateEventNodeWithInfo(doc, newFields);
                    elementsToAppendChildTo.add(eventParent);
                    elementsToAppendChildBefore.add(event.getNextSibling());
                    elementsToAppend.add(newNode);
                }

                return false;
            } else {
                return true;
            }
        }

        @Override
        public void doFinalAction() {
            for (int i = 0; i < elementsToAppend.size(); i++) {
                Node toAppend = elementsToAppend.get(i);
                Node toPutBefore = elementsToAppendChildBefore.get(i);
                Node toAppendTo = elementsToAppendChildTo.get(i);
                toAppendTo.insertBefore(toAppend, toPutBefore);
            }
        }
    }

    /**
     * https://tools.ietf.org/html/rfc6321#appendix-A
     * similar to implementation of ical/xcal recurrence parsing.
     * however, most entries use space separated lists rather than separate elements
     */
    public static LinkedHashSet<Date> generateDatesForRecurrencePattern(Date dtStart, Node rrule) throws
            ParserConfigurationException, TransformerException, IOException, XPathExpressionException, SAXException,
            XPathFactoryConfigurationException, ParseException {
        String freqStr = null, untilStr = null;
        Integer count = null, interval = null;
        String[] byday = null;
        int[] bysecond = null, byminute = null, byhour = null, bymonthday = null, byyearday = null, byweekno = null,
                bymonth = null, bysetpos = null;

        NodeList children = rrule.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String content = child.getTextContent();
            switch (child.getNodeName()) {
                case "freq": freqStr = content; break;
                case "until": untilStr = content; break;
                case "count": count = Integer.parseInt(content); break;
                case "interval": interval = Integer.parseInt(content); break;
                case "bysecond": bysecond = Utility.splitStringToIntArr(content, " "); break;
                case "byminute": byminute = Utility.splitStringToIntArr(content, " "); break;
                case "byhour": byhour = Utility.splitStringToIntArr(content, " "); break;
                case "byday": byday = content.split(" "); break;
                case "bymonthday": bymonthday = Utility.splitStringToIntArr(content, " "); break;
                case "byyearday": byyearday = Utility.splitStringToIntArr(content, " "); break;
                case "byweekno ": byweekno = Utility.splitStringToIntArr(content, " "); break;
                case "bymonth": bymonth = Utility.splitStringToIntArr(content, " "); break;
                case "bysetpos": bysetpos = Utility.splitStringToIntArr(content, " "); break;
            }
        }

        Date until = null;
        if (untilStr != null) {
            until = Utility.isoDateFormatter.parse(untilStr);
        }
        RecurrenceParameters recParams = new RecurrenceParameters(
                freqStr,count,interval,until,byday,bysecond,byminute,byhour,bymonthday,byyearday,byweekno,bymonth,bysetpos);
        return generateDatesForRecurrencePattern(dtStart,recParams);
    }

    public static final int USE_COUNT = 0x1;
    public static final int USE_UNTIL = 0x2;

    private static boolean areWeDoneYet(Integer count, Date date, Integer totCount, Date until, int WHICHTOUSE) {
        if(WHICHTOUSE == USE_COUNT) {
            if (count >= totCount) {
                return true;
            } else {
                return false;
            }
        } else if (WHICHTOUSE == USE_UNTIL ){
            if (date.compareTo(until) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new RuntimeException("invalid parameter");
        }
    }

    public static final int MAXMAXCOUNT = 10000;

    public static Date getDefaultMaxDate() {
        GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
        calendar.add(Calendar.YEAR,1);
        return calendar.getTime();
    }

    public static LinkedHashSet<Date> generateDatesForRecurrencePattern(Date dtStart, RecurrenceParameters recParams)  {
        GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance();
        calendar.setTime(dtStart);
        final int WHICHTOUSE;
        int maxCount;
        Date maxDate;

        if(recParams.until == null) {
            maxDate = getDefaultMaxDate();
        } else {
            maxDate = recParams.until;
        }
        if(recParams.count == null) {
            WHICHTOUSE = USE_UNTIL;
            maxCount = MAXMAXCOUNT;
        } else {
            maxCount = recParams.count;
            WHICHTOUSE = USE_COUNT;
        }

        int FIELDTOITERATE;
        switch (recParams.freqStr) {
            case "SECONDLY": FIELDTOITERATE = Calendar.SECOND; break;
            case "MINUTELY": FIELDTOITERATE = Calendar.MINUTE; break;
            case "HOURLY": FIELDTOITERATE = Calendar.HOUR; break;
            case "DAILY": FIELDTOITERATE = Calendar.DATE; break;
            case "WEEKLY": FIELDTOITERATE = Calendar.WEEK_OF_YEAR; break;
            case "MONTHLY": FIELDTOITERATE = Calendar.MONTH; break;
            case "YEARLY": FIELDTOITERATE = Calendar.YEAR; break;
            default: FIELDTOITERATE = Calendar.DATE;
        }

        int count = 0;
        LinkedHashSet<Date> out = new LinkedHashSet<>();
        Integer interval = recParams.interval;
        if(interval == null) {
            interval = 1;
        }
        int maxIterations = maxCount*5; //CB 5 is a random magic number.
        int numIterations = 0;
        while(!areWeDoneYet(count,calendar.getTime(),recParams.count,recParams.until,WHICHTOUSE)) {
            if(numIterations > maxIterations) {
                break;
            }
            numIterations++;
            List<Date> addPossibilities = new ArrayList<>();

            GregorianCalendar nCal = (GregorianCalendar) calendar.clone();
            if(recParams.bysecond == null && recParams.byminute == null && recParams.byhour == null &&
                    recParams.byday == null && recParams.bymonthday == null && recParams.byyearday == null
                    && recParams.byweekno == null && recParams.bymonth == null) {
                addPossibilities.add(nCal.getTime());
            } else {
                if (recParams.bysecond != null) {
                    for (int n : recParams.bysecond) {
                        nCal.add(Calendar.SECOND, n);
                        addPossibilities.add(nCal.getTime());
                        nCal = (GregorianCalendar) calendar.clone();
                    }
                }
                if (recParams.byminute != null) {
                    for (int n : recParams.byminute) {
                        nCal.add(Calendar.MINUTE, n);
                        addPossibilities.add(nCal.getTime());
                        nCal = (GregorianCalendar) calendar.clone();
                    }
                }
                if (recParams.byhour != null) {
                    for (int n : recParams.byhour) {
                        nCal.add(Calendar.HOUR, n);
                        addPossibilities.add(nCal.getTime());
                        nCal = (GregorianCalendar) calendar.clone();
                    }
                }
                if (recParams.byday != null) {
                    for (String s : recParams.byday) {
                        int dNum = dayOfWeekStrToInt(s);
                        Date nextDay = getNextDateWithDay(nCal.getTime(), dNum);
                        addPossibilities.add(nextDay);
                    }
                }
                if (recParams.bymonthday != null) {
                    for (int n : recParams.bymonthday) {
                        int dom;
                        if (n < 0) {
                            int max = nCal.getActualMaximum(Calendar.DAY_OF_MONTH);
                            dom = max - n;
                        } else {
                            dom = n;
                        }
                        nCal.set(Calendar.DAY_OF_MONTH, dom);
                        addPossibilities.add(nCal.getTime());
                    }
                }
                if (recParams.byyearday != null) {
                    for (int n : recParams.byyearday) {
                        int dom;
                        if (n < 0) {
                            int max = nCal.getActualMaximum(Calendar.DAY_OF_YEAR);
                            dom = max - n;
                        } else {
                            dom = n;
                        }
                        nCal.set(Calendar.DAY_OF_YEAR, dom);
                        addPossibilities.add(nCal.getTime());
                    }
                }
                if (recParams.byweekno != null) {
                    for (int n : recParams.byweekno) {
                        int dom;
                        if (n < 0) {
                            int max = nCal.getActualMaximum(Calendar.WEEK_OF_YEAR);
                            dom = max - n;
                        } else {
                            dom = n;
                        }
                        nCal.set(Calendar.WEEK_OF_YEAR, dom);
                        addPossibilities.add(nCal.getTime());
                    }
                }
                if (recParams.bymonth != null) {
                    for (int n : recParams.bymonth) {
                        nCal.set(Calendar.MONTH, n);
                        addPossibilities.add(nCal.getTime());
                    }
                }
            }
            Collections.sort(addPossibilities);
            if (recParams.bysetpos != null) {
                for (int n : recParams.bysetpos) {
                    Date d = addPossibilities.get(n);
                    if(count < maxCount) {
                        if(d.compareTo(maxDate)<0) {
                            boolean added = out.add(d);
                            if(added) {
                                count++;
                            }
                        }
                    }
                }
            } else {
                for (Date d : addPossibilities) { //CB TODO FIX: I believe there are certain ways to get the generator stuck in a loop.
                    if(count < maxCount) {
                        if(d.compareTo(maxDate)<0) {
                            boolean added = out.add(d);
                            if(added) {
                                count++;
                            }
                        }
                    }
                }
            }
            calendar.add(FIELDTOITERATE,interval);
        }

        return out;
    }

    public static int dayOfWeekStrToInt(String dayOfWeek) {
        switch(dayOfWeek) {
            case "SU": return 0;
            case "MO": return 1;
            case "TU": return 2;
            case "WE": return 3;
            case "TH": return 4;
            case "FR": return 5;
            case "SA": return 6;
            default:
                return -1;
        }
    }

    public static Date getNextDateWithDay(Date startDate, int dayOfWeekToGet) {
        GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
        cal.setTime(startDate);
        //0 -- SUNDAY. 6 -- SATURDAY
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)-1;
        int offset = dayOfWeekToGet-dayOfWeek;
        cal.add(Calendar.DATE,offset);
        return cal.getTime();
    }

    public static class RecurrenceParameters {
        public String freqStr = null;
        public Integer count = null, interval = null;
        public Date until;
        public String[] byday = null;
        public int[] bysecond = null, byminute = null, byhour = null, bymonthday = null, byyearday = null, byweekno = null,
                bymonth = null, bysetpos = null;

        public RecurrenceParameters(String freqStr, Integer count, Integer interval, Date until,
                String[] byday, int[] bysecond, int[] byminute, int[] byhour, int[] bymonthday, int[] byyearday,
                                    int[] byweekno, int[] bymonth, int[] bysetpos) {
            this.freqStr = freqStr;
            this.count = count;
            this.interval = interval;
            this.until = until;
            this.byday = byday;
            this.bysecond = bysecond;
            this.byminute = byminute;
            this.byhour = byhour;
            this.bymonthday = bymonthday;
            this.byyearday = byyearday;
            this.byweekno = byweekno;
            this.bymonth = bymonth;
            this.bysetpos = bysetpos;
        }
    }


    public static class SimpleEventFields {
        public String name;
        public Date dateTime;
        public Duration duration;
        public String description;

        public SimpleEventFields(String name, Date dateTime, Duration duration, String description) {
            this.name = name;
            this.dateTime = dateTime;
            this.duration = duration;
            this.description = description;
        }
        public SimpleEventFields(SimpleEventFields base, Date newDate) {
            this.name = base.name;
            this.dateTime = newDate;
            this.duration = duration;
            this.description = description;
        }
    }

    public String getEffectiveCalendarAsString() throws DatatypeConfigurationException, TransformerException, SAXException, XPathExpressionException, SaxonApiException, ParserConfigurationException, XPathFactoryConfigurationException, IOException {
        return Utility.nodeToString(getEffectiveCalendar());
    }

    public Document getEffectiveCalendar() throws ParserConfigurationException, SAXException, IOException,
            XPathFactoryConfigurationException, SaxonApiException, XPathExpressionException, TransformerException, DatatypeConfigurationException {
        Document parsedCalendar = getParsedCalendar();

        ItemRecurrenceHandler recHandler = new ItemRecurrenceHandler(parsedCalendar);
        DomVisitingHelper.domVisitTree(parsedCalendar, recHandler);
        recHandler.doFinalAction();

        return parsedCalendar;
    }

    public Document getParsedCalendar() throws IOException, SAXException, ParserConfigurationException {
        String calendarStr = filesManager.getFileContents("calendar.xml");
        InputStream asStream = new ByteArrayInputStream(calendarStr.getBytes("UTF-8"));
        Document parsedCal = Utility.parseStreamToDOM(asStream);
        return parsedCal;
    }

}

package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.Anchor;
import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.berrypim.calendar.EffectiveCalendarGenerator;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;
import net.sf.saxon.s9api.XdmSequenceIterator;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/29/2016.
 */
public class DashboardController extends PIMDefaultController {

    public String virtualFileName = "quickNotes.xml";
    private RawController rawController;
    private CalendarController calendarController;
    private EffectiveCalendarGenerator effectiveCalendarGenerator;
    private DataFilesManager filesManager;

    public DashboardController(String controllerBase) {
        this.controllerBase = controllerBase;
        rawController = new RawController(controllerBase);
        String controllerBaseGroup = controllerBase.split("/")[0]+"/";
        calendarController = new CalendarController(controllerBase,controllerBaseGroup);
        filesManager = Anchor.getInstance().getDataFilesManager();
        effectiveCalendarGenerator = new EffectiveCalendarGenerator(filesManager);
    }

    @Override
    public List<Map.Entry<String, String>> getTopTabsItems() {
        String[] starter = {
                "home", "Home",
        };
        return Utility.tupleizeArray(starter);
    }

    @Override
    public String fill_contentPane(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                   AuthInfoHolder authInfo) throws Exception {
        Document effectiveCalendar = effectiveCalendarGenerator.getEffectiveCalendar();
        StringBuilder out = new StringBuilder();
        out.append("<h2>Week at a Glance</h2>");
        out.append("<div class='twothirdswidth'>");
        out.append("<h3>Calendar events this week</h3>");
        out.append("<pre>");
        String calQuery = "for $t in //vevent " +
                "let $mdate := $t//dtstart/date-time " +
                "order by $mdate " +
                "where $mdate gt string(current-dateTime()) " +
                "and $mdate lt string(current-dateTime() + xs:dayTimeDuration('P7D')) " +
                "return $t";
//    "let $d1 := xs:date('2014-03-02') " +
//            "let $d2 := xs:date('2014-04-21') " +
//            "let $days:=0 to days-from-duration($d2 - $d1) " +
//            "return $days!($d1+ . *xs:dayTimeDuration('P1D'))"
        String eCalResults = Utility.runXQueryOnDocument(effectiveCalendar,calQuery);
        out.append(Utility.escapeXML(eCalResults));
//        XdmSequenceIterator iterator = Utility.runXQueryOnDOMDocument(effectiveCalendar, calQuery);
//        while (iterator.hasNext()) {
//            out.append(iterator.next().getStringValue());
//        }
        String todoStr = filesManager.getFileContents("todo.xml");
        out.append("</pre>");
        out.append("</div>");
        out.append("<div class='onethirdwidth'>");
        out.append("<h3>Next 10 Todos</h3>");
        out.append("<pre>");
        String todoQuery = "(for $t in //todo " +
                "order by $t/@due ascending " +
                "return ($t/@due/string() , ' ', $t/name ,' &#10;')" +
                ")[position() = 1 to 40]";
        //String eCalResults = Utility.runXQueryOnDocument(effectiveCalendar,calQuery);
        //out.append(eCalResults);
        XdmSequenceIterator iterator2 = Utility.runXQueryOnStringToDS(todoStr, todoQuery);
        while (iterator2.hasNext()) {
            out.append(Utility.escapeXML(iterator2.next().getStringValue()));
        }
        out.append("</pre>");
        out.append("</div>");
        out.append("<div style='clear: both'></div>");
        out.append("<span id='fileName' style='display: none'>" + virtualFileName +"</span>");
        return out.toString();
    }

    @Override
    public String fill_rightSideList(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                     AuthInfoHolder authInfo) throws Exception {
        String fileContents = filesManager.getFileContents(virtualFileName+".bPIMD");
        return rawController.buildRightSideGivenContents(fileContents);
    }

    public String api_save(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                           AuthInfoHolder authInfo) throws IOException, InterruptedException {
        String fileNameToSave = Utility.getFirstQParamResult(queryParams, "file");
        if(fileNameToSave.endsWith(".bPIMD")) {
             String doGITCommitStr = Utility.getFirstQParamResult(queryParams, "doGITCommit");
             return rawController.saveFile(fileNameToSave, doGITCommitStr, dataBody);
        } else {
            return "bypassed save";
        }

    }

}

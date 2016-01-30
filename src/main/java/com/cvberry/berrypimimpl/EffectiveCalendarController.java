package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.Anchor;
import com.cvberry.berrypim.ControllerObject;
import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.berrypim.DefaultController;
import com.cvberry.berrypim.calendar.EffectiveCalendarGenerator;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;
import org.w3c.dom.Document;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/29/2016.
 */
public class EffectiveCalendarController extends PIMDefaultController {

    public String virtualFileName = "effectiveCalendar.xml";
    private RawController rawController;
    private CalendarController calendarController;
    private EffectiveCalendarGenerator effectiveCalendarGenerator;
    private DataFilesManager filesManager;

    public EffectiveCalendarController(String controllerBase) {
        this.controllerBase = controllerBase;
        rawController = new RawController(controllerBase);
        String controllerBaseGroup = controllerBase.split("/")[0]+"/";
        calendarController = new CalendarController(controllerBase,controllerBaseGroup);
        filesManager = Anchor.getInstance().getDataFilesManager();
        effectiveCalendarGenerator = new EffectiveCalendarGenerator(filesManager);
    }

    @Override
    public List<Map.Entry<String, String>> getTopTabsItems() {
        return calendarController.getTopTabsItems();
    }

    @Override
    public String fill_contentPane(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                   AuthInfoHolder authInfo) throws Exception {
        StringBuilder out = new StringBuilder();
        String effectiveCalendarStr = effectiveCalendarGenerator.getEffectiveCalendarAsString();
        String actionStr = Utility.getFirstQParamResult(queryParams, "action");
        String dataStr = Utility.getFirstQParamResult(queryParams, "data");
        rawController.displayEditorForFile(out, effectiveCalendarStr, virtualFileName, actionStr, dataStr, true);
        return out.toString();
    }
    @Override
    public String fill_topTabsItems(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                    AuthInfoHolder authInfo) {
        return calendarController.fill_topTabsItems(pathComponents,queryParams,dataBody,authInfo);
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

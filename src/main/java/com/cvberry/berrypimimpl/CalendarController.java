package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.ControllerObject;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;

import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class CalendarController extends RawController implements ControllerObject {
    String controllerBaseGroup;

    public CalendarController(String controllerBase, String controllerBaseGroup) {
        super(controllerBase);
        this.controllerBaseGroup = controllerBaseGroup;
        UNIVERSALFILENAME = "calendar.xml";
    }

    @Override
    public List<Map.Entry<String, String>> getTopTabsItems() {
        String[] starter = {
                "home", "Raw Calendar",
                "effective", "Effective Calendar",
        };
        return Utility.tupleizeArray(starter);
    }

    public String fill_topTabsItems(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                    AuthInfoHolder authInfo) {
        List<Map.Entry<String, String>> items = getTopTabsItems();
        String selectorPath = (pathComponents.length < 2 || pathComponents[1] == null || pathComponents[1].isEmpty()) ?
                items.get(0).getKey() : pathComponents[1];
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, String> entry : items) {
            String key = entry.getKey();
            if (key.toLowerCase().equals(selectorPath.toLowerCase())) {
                out.append("<li class='active'><a href='" + rootPathStr + controllerBaseGroup + entry.getKey() + "'>" + entry.getValue() + "</a></li>\n");
            } else {
                out.append("<li><a href='" + rootPathStr + controllerBaseGroup + entry.getKey() + "'>" + entry.getValue() + "</a></li>\n");
            }
        }
        return out.toString();
    }

}

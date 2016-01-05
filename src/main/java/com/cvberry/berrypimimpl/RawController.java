package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.Anchor;
import com.cvberry.berrypim.ControllerObject;
import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.util.Utility;
import com.sun.management.OperatingSystemMXBean;

import java.io.File;
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
        String[] starter = new String[fileNames.size()*2];
        for (int i = 0; i < starter.length-1; i+=2) {
            starter[i]=fileNames.get(i/2);
            starter[i+1]=fileNames.get(i/2);
        }

        return Utility.tupleizeArray(starter);
    }

    public String fill_contentPane(String[] pathComponents, String queryStr) {
        StringBuilder out = new StringBuilder();
        String selectorPath = Utility.getPathComponentOrDefault(pathComponents,1,getTopTabsItems().get(0).getKey());

        String fileContents = filesManager.getFileContents(selectorPath);
        out.append("<textarea class='fullsize'>");
        out.append(fileContents);
        out.append("</textarea>");
        return out.toString();
    }
}

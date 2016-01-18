package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.berrypim.PageComponent;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;

import javax.xml.crypto.Data;
import java.util.Map;

/**
 * Created by vancan1ty on 1/18/2016.
 */
public class RightSideNoteComponent implements PageComponent {

    public String fileName;
    public DataFilesManager filesManager;

    public RightSideNoteComponent(String fileName, DataFilesManager filesManager) {
        this.fileName = fileName;
        this.filesManager = filesManager;
    }

    @Override
    public String makeContentPaneHTML(String[] pathComponents, Map<String, String[]> queryParams, String dataBody, AuthInfoHolder authInfo) throws Exception {
        String fileContents = filesManager.getFileContents(fileName + ".bPIMD");
        StringBuilder out = new StringBuilder();
        out.append("<div id='rsCSSHelper'>");
        out.append("<label>Notes / XPath Queries</label>");
        boolean helperInitialized = false;
        out.append("<ul id='savedQueries' contentEditable='true'>");
        if (fileContents != null) {
            helperInitialized = true;
            String[] lines = fileContents.trim().split("\n");
            for (String s : lines) {
                out.append("<li>" + Utility.escapeXML(s) + "</li>");
            }
        } else {
            out.append("<li> </li>");
        }
        out.append("</ul>");
        out.append("<span style='display:none' id='helperInitialized'>" + helperInitialized + "</span>");
        out.append("</div>");
        return out.toString();
    }
}

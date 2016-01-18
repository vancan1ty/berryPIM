package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.berrypim.PageComponent;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;

import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/18/2016.
 */
public class FileSelectorComponent implements PageComponent {

    public String fileSuffix;
    public DataFilesManager filesManager;
    public List<String> fileNames;

    public FileSelectorComponent(String fileSuffix, DataFilesManager filesManager) {
        this.fileSuffix = fileSuffix;
        this.filesManager = filesManager;
    }

    public String getSelectedFileFromURL(Map<String,String[]> queryParams) {
        return Utility.getFirstQParamResult(queryParams,"file");
    }

    @Override
    public String makeContentPaneHTML(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                      AuthInfoHolder authInfo) {
        StringBuilder out = new StringBuilder();
        fileNames = filesManager.listFilesEndingWithStr(fileSuffix);
        out.append("<select id='fileselector' name='fileselector' onchange='doFileSelection()'>\n");
        for (String file : fileNames) {
                out.append("<option value='" + file + "'>" + file + "</option>");
        }
        out.append("</select>\n");
        return out.toString();
    }
}

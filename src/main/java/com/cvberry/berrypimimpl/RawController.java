package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.Anchor;
import com.cvberry.berrypim.ControllerObject;
import com.cvberry.berrypim.DataFilesManager;
import com.cvberry.berrypim.GitManager;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class RawController extends PIMDefaultController implements ControllerObject {

    Anchor myAnchor;
    DataFilesManager filesManager;
    public String UNIVERSALFILENAME = null;

    public RawController(String controllerBase) {
        myAnchor = Anchor.getInstance();
        filesManager = myAnchor.getDataFilesManager();
        this.controllerBase = controllerBase;
    }

    @Override
    public List<Map.Entry<String, String>> getTopTabsItems() {
        List<String> fileNames = filesManager.listDataFiles();
        String[] starter = new String[fileNames.size() * 2];
        for (int i = 0; i < starter.length - 1; i += 2) {
            starter[i] = fileNames.get(i / 2);
            starter[i + 1] = fileNames.get(i / 2);
        }

        return Utility.tupleizeArray(starter);
    }

    public String getFileName(String[] pathComponents) {
        return Utility.getPathComponentOrDefault(pathComponents, 1, getTopTabsItems().get(0).getKey());
    }

    public String getFunction(String[] pathComponents) {
        return Utility.getPathComponentOrDefault(pathComponents, 1, getTopTabsItems().get(0).getKey());
    }

    @Override
    public String fill_contentPane(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                   AuthInfoHolder authInfo)
            throws SAXException, ParserConfigurationException, XPathExpressionException, IOException, TransformerException, GitAPIException {
        StringBuilder out = new StringBuilder();
        String fileName;
        if (UNIVERSALFILENAME != null) {
           fileName = UNIVERSALFILENAME;
        } else {
            fileName = getFileName(pathComponents);
        }
        String actionStr=Utility.getFirstQParamResult(queryParams,"action");
        if (actionStr!=null && actionStr.equals("reload")) {
            filesManager.readInAllFiles();
        }
        if (actionStr!=null && actionStr.equals("sync")) {
            GitManager gitManager = filesManager.gitManager;
            gitManager.syncToGit(authInfo.truePassword);
            filesManager.readInAllFiles();
        }
        String dataStr = Utility.getFirstQParamResult(queryParams,"data");
        displayEditorForFile(out, fileName, actionStr, dataStr);
        return out.toString();
    }

    public String api_save(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                           AuthInfoHolder authInfo) throws IOException, InterruptedException {
            String fileNameToSave = Utility.getFirstQParamResult(queryParams,"file");
            String doGITCommitStr = Utility.getFirstQParamResult(queryParams,"doGITCommit");
            if (fileNameToSave == null) {
                throw new RuntimeException("file save failed! -- name of file not specified.");
            }
            if (fileNameToSave.endsWith(".bPIMD")) {//this is one of our special helper files
                //only save if file already exists or if the string is non-empty
                String oldContents=filesManager.getFileContents(fileNameToSave);
                if(dataBody != null && (oldContents!=null || !dataBody.isEmpty())) {//then we should save

                } else {
                    return "no save necessary for this non-existent helper file.";
                }
            }
            StringBuilder resultsStrb = new StringBuilder();
            boolean doGitCommit = (doGITCommitStr != null && doGITCommitStr.equals("true")) ? true : false;
            boolean success =  filesManager.saveNewContentsToFile(fileNameToSave,dataBody,resultsStrb,false,doGitCommit);
            boolean success2= filesManager.readInAllFilesSafe(resultsStrb);
            if(success && success2) {
                return resultsStrb.toString();
            } else {
                throw new RuntimeException("file save failed! " + resultsStrb.toString());
            }
    }

    public void displayEditorForFile(StringBuilder out, String fileName, String action, String dataStr) throws SAXException, TransformerException,
            ParserConfigurationException, XPathExpressionException, IOException {
                String fileContents = filesManager.getFileContents(fileName);
        out.append("<span id='fileName' style='display:none'>"+fileName+"</span>");
        out.append("<textarea class='fullsize' id='mainEditor'>");
        out.append(fileContents);
        out.append("</textarea><br>");

        if (fileName.endsWith(".xml") || fileName.endsWith(".xsd")) {
            out.append("<b>Run XPath</b>\n");
            if (action != null && action.equals("xpath")) {
                out.append("<form>\n");
                out.append("<input type='hidden' name='action' value='xpath'></input>");
                out.append("<input class='wideinput' name='data' value='" + dataStr + "'></input>");
                out.append("<input type='submit'></input>");
                out.append("</form>\n");
                out.append("results:<br>\n");
                out.append("<pre>\n");
                String results = Utility.runXPathOnString(fileContents, dataStr);
                String escapedResults = Utility.escapeXML(results);
                out.append(escapedResults);
                out.append("</pre>");
            } else {
                out.append("<form>\n");
                out.append("<input type='hidden' name='action' value='xpath'></input>");
                out.append("<input name='data'></input>");
                out.append("<input type='submit'></input>");
                out.append("</form>\n");
            }
        }
        out.append("<script src="+myAnchor.getRootPath()+"\"/static/js/rawEditor.js\"></script>");
    }

    @Override
    public String fill_rightSideList(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                     AuthInfoHolder authInfo) throws Exception {
        String fileName;
        if (UNIVERSALFILENAME != null) {
           fileName = UNIVERSALFILENAME;
        } else {
            fileName = getFileName(pathComponents);
        }
        String fileContents = filesManager.getFileContents(fileName+".bPIMD");
        StringBuilder out = new StringBuilder();
        out.append("<div id='rsCSSHelper'>");
        out.append("<label>Notes / XPath Queries</label>");
        boolean helperInitialized = false;
        out.append("<ul id='savedQueries' contentEditable='true'>");
        if(fileContents!=null) {
            helperInitialized = true;
            String[] lines = fileContents.trim().split("\n");
            for (String s : lines) {
                out.append("<li>"+Utility.escapeXML(s)+"</li>");
            }
        } else {
            out.append("<li> </li>");
        }
        out.append("</ul>");
        out.append("<span style='display:none' id='helperInitialized'>"+helperInitialized+"</span>");
        out.append("</div>");
        return out.toString();
    }
}

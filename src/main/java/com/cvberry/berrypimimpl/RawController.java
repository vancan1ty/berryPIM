package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.*;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;
import net.sf.saxon.s9api.SaxonApiException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class RawController extends PIMDefaultController implements ControllerObject {

    Anchor myAnchor;
    DataFilesManager filesManager;
    public String UNIVERSALFILENAME = null;
    public Templater templater;

    public RawController(String controllerBase) {
        myAnchor = Anchor.getInstance();
        filesManager = myAnchor.getDataFilesManager();
        this.controllerBase = controllerBase;
        this.templater = myAnchor.getTemplater();
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

    public String getFileName(String[] pathComponents, Map<String, String[]> queryParams) {
        return Utility.getPathComponentOrDefault(pathComponents, 1, getTopTabsItems().get(0).getKey());
    }

    public String getFunction(String[] pathComponents) {
        return Utility.getPathComponentOrDefault(pathComponents, 1, getTopTabsItems().get(0).getKey());
    }

    @Override
    public String fill_contentPane(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                   AuthInfoHolder authInfo) throws IOException, GitAPIException, ParserConfigurationException, TransformerException, XPathExpressionException, SaxonApiException, SAXException, XPathFactoryConfigurationException {
        StringBuilder out = new StringBuilder();
        String fileName;
        if (UNIVERSALFILENAME != null) {
            fileName = UNIVERSALFILENAME;
        } else {
            fileName = getFileName(pathComponents, queryParams);
        }
        String actionStr = Utility.getFirstQParamResult(queryParams, "action");
        if (actionStr != null && actionStr.equals("reload")) {
            filesManager.readInAllFiles();
        }
        if (actionStr != null && actionStr.equals("sync")) {
            GitManager gitManager = filesManager.gitManager;
            gitManager.syncToGit(authInfo.truePassword);
            filesManager.readInAllFiles();
        }
        String dataStr = Utility.getFirstQParamResult(queryParams, "data");
        String fileContents = filesManager.getFileContents(fileName);
        displayEditorForFile(out, fileContents, fileName, actionStr, dataStr, false);
        return out.toString();
    }

    public String api_save(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                           AuthInfoHolder authInfo) throws IOException, InterruptedException {
        String fileNameToSave = Utility.getFirstQParamResult(queryParams, "file");
        String doGITCommitStr = Utility.getFirstQParamResult(queryParams, "doGITCommit");
        return saveFile(fileNameToSave, doGITCommitStr, dataBody);
    }

    public String saveFile(String fileNameToSave, String doGITCommitStr, String dataBody) throws IOException, InterruptedException {
        if (fileNameToSave == null) {
            throw new RuntimeException("file save failed! -- name of file not specified.");
        }
        if (fileNameToSave.endsWith(".bPIMD")) {//this is one of our special helper files
            //only save if file already exists or if the string is non-empty
            String oldContents = filesManager.getFileContents(fileNameToSave);
            if (dataBody != null && (oldContents != null || !dataBody.isEmpty())) {//then we should save

            } else {
                return "no save necessary for this non-existent helper file.";
            }
        }
        StringBuilder resultsStrb = new StringBuilder();
        boolean doGitCommit = (doGITCommitStr != null && doGITCommitStr.equals("true")) ? true : false;
        boolean success = filesManager.saveNewContentsToFile(fileNameToSave, dataBody, resultsStrb, false, doGitCommit);
        boolean success2 = filesManager.readInAllFilesSafe(resultsStrb);
        if (success && success2) {
            return resultsStrb.toString();
        } else {
            throw new RuntimeException("file save failed! " + resultsStrb.toString());
        }
    }

    /**
     * @param out
     * @param fileContents
     * @param fileName     is kind of a fake "fileName" -- doesn't have to correspond to real file
     * @param action
     * @param dataStr
     * @param readOnly
     * @throws SAXException
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     * @throws XPathFactoryConfigurationException
     * @throws SaxonApiException
     */
    public void displayEditorForFile(StringBuilder out, String fileContents, String fileName,
                                     String action, String dataStr, boolean readOnly)
            throws SAXException, TransformerException,
            ParserConfigurationException, XPathExpressionException, IOException, XPathFactoryConfigurationException, SaxonApiException {
        out.append("<span id='fileName' style='display:none'>" + fileName + "</span>");
        String readOnlyStr = readOnly ? "readonly='readonly'" : "";
        out.append("<textarea class='fullsize' id='mainEditor'" + readOnlyStr + ">");
        out.append(fileContents);
        out.append("</textarea><br>");

        if (fileName.endsWith(".xml") || fileName.endsWith(".xsd")) {
            out.append("<b>Run XQuery</b>\n");
            if (action != null && action.equals("xpath")) {
                out.append("<input id='xpathdata' class='wideinput' name='data' value='" + dataStr + "'></input>");
                out.append("<button onclick='submitXPATH()'>Submit</button>");
                out.append("<br>results:<br>\n");
                out.append("<pre>\n");
                String results = Utility.runXQueryOnString(fileContents, dataStr);
                String escapedResults = Utility.escapeXML(results);
                out.append(escapedResults);
                out.append("</pre>");
            } else {
                out.append("<input id='xpathdata' class='wideinput' name='data'></input>");
                out.append("<button onclick='submitXPATH()'>Submit</button>");
            }
        }
        out.append(templater.getTemplateContents("rawEditorLoad.html"));
    }

    @Override
    public String fill_rightSideList(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                     AuthInfoHolder authInfo) throws Exception {
        String fileName;
        if (UNIVERSALFILENAME != null) {
            fileName = UNIVERSALFILENAME;
        } else {
            fileName = getFileName(pathComponents, queryParams);
        }
        String fileContents = filesManager.getFileContents(fileName + ".bPIMD");
        return buildRightSideGivenContents(fileContents);
    }

    public String buildRightSideGivenContents(String fileContents) {
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

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
public class GraphsController extends PIMDefaultController implements ControllerObject {

    Anchor myAnchor;
    DataFilesManager filesManager;
    public String UNIVERSALFILENAME = null;
    public Templater templater;

    public GraphsController(String controllerBase) {
        myAnchor = Anchor.getInstance();
        filesManager = myAnchor.getDataFilesManager();
        this.controllerBase = controllerBase;
        this.templater = myAnchor.getTemplater();
    }

    @Override
    public List<Map.Entry<String, String>> getTopTabsItems() {
        return Utility.tupleizeArray(new String[]{"home", "Graphs Home"});
    }

    public String getFunction(String[] pathComponents) {
        return Utility.getPathComponentOrDefault(pathComponents, 1, getTopTabsItems().get(0).getKey());
    }

    @Override
    public String fill_contentPane(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                   AuthInfoHolder authInfo)
            throws Exception {
        StringBuilder out = new StringBuilder();


        String actionStr = Utility.getFirstQParamResult(queryParams, "action");
        String dataStr = Utility.getFirstQParamResult(queryParams, "data");
        FileSelectorComponent fileSelector = new FileSelectorComponent(".xml", filesManager);
        String fileSelectHTML = fileSelector.makeContentPaneHTML(pathComponents, queryParams, dataBody, authInfo);
        out.append(fileSelectHTML + "\n");

        String fileName = null;
        if (queryParams.containsKey("file")) {
            fileName = queryParams.get("file")[0];
        } else {
            fileName = fileSelector.fileNames.get(0);
        }

        GraphsComponent graphComp = new GraphsComponent(fileName, templater,filesManager);
        String graphCompHTML = graphComp.makeContentPaneHTML(pathComponents, queryParams, dataBody, authInfo);
        out.append(graphCompHTML + "\n");

        return out.toString();
    }

    @Override
    public String fill_rightSideList(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                     AuthInfoHolder authInfo) throws Exception {
        RightSideNoteComponent noteList = new RightSideNoteComponent("graphs.bPIMD", filesManager);
        return noteList.makeContentPaneHTML(pathComponents, queryParams, dataBody, authInfo);
    }
}

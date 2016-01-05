package com.cvberry.berrypim;

import com.cvberry.util.ResourceLister;
import com.cvberry.util.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class DefaultConfigurator implements Configurator {

    public void doConfiguration(String rootPath) throws IOException, URISyntaxException {
        System.out.println("howdy!");
        Anchor myAnchor = Anchor.getInstance();

        myAnchor.setAppName("berryPIM");

        String prospectivePimFilesRoot = System.getProperty("BERRYPIM_DATA_ROOT");
        if (prospectivePimFilesRoot == null) {
            myAnchor.setPIMFilesRoot("berryData");
        } else {
            myAnchor.setPIMFilesRoot(prospectivePimFilesRoot);
        }

        DataFilesManager filesManager = new DataFilesManager(myAnchor.getPIMFilesRoot());
        myAnchor.setDataFilesManager(filesManager);

        System.out.println("templates");
        List<String> templateFileNames = Arrays.stream(ResourceLister
                .getResourceListingForPath(this.getClass(),"templates/")).map((String s)-> "templates/"+s).collect(Collectors.toList());
        System.out.println(Arrays.toString(templateFileNames.toArray(new String[0])));
        Templater templater = new Templater(rootPath);
        templater.registerTemplates(templateFileNames);
        myAnchor.setTemplater(templater);
        //System.out.println(Arrays.toString(ResourceLister.getResourceListingForPath(this.getClass(),"templates/")));
        //System.out.println(ResourceLister.getResources(Pattern.compile(".*")).stream().filter((s)-> s.contains("templates")).collect(Collectors.joining("\n")).toString());

        myAnchor.setDispatcher(new Dispatcher());
        DefaultController dController = new DefaultController();
        myAnchor.getDispatcher().addDispatchPath("",dController);
        myAnchor.getDispatcher().addDispatchPath("/",dController);
    }
}

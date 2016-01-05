package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.Anchor;
import com.cvberry.berrypim.Configurator;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class PIMConfigurator implements Configurator {
    @Override
    public void doConfiguration(String rootPath) throws IOException, URISyntaxException {
        Anchor myAnchor = Anchor.getInstance();
        myAnchor.getDispatcher().addDispatchPath("dashboard",new DashboardController("dashboard/"));
        myAnchor.getDispatcher().addDispatchPath("raw",new RawController("raw/"));
        myAnchor.getDispatcher().addDispatchPath("",new DashboardController("dashboard/"));
    }

}

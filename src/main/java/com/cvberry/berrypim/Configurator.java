package com.cvberry.berrypim;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by vancan1ty on 1/3/2016.
 *
 * Configured configurators by main servlet after it is deployed.
 */
public interface Configurator {
    public void doConfiguration(String rootPath) throws IOException, URISyntaxException;
}

package com.cvberry.berrypim;

import java.io.InputStream;

/**
 * Created by vancan1ty on 1/8/2016.
 * The responsibility of this class is to retrieve the contents of the primary configuration xml file, usually
 * either 'config.xml' or 'mconfig.xml'
 */
public class ConfigXMLFileFinder {

    /**
     * Obtains the relevant 'config' xml file contents. <br>
     * The logic is as follows:<br>
     * 1. if '/mconfig.xml' is found on the classpath, that is used.<br>
     * 2. otherwise, the default '/config.xml' is used.<br>
     *
     * @return the contents of the 'config' xml file as an InputStream.
     */
    public static InputStream getConfigXMLStream() {
        InputStream opt1 = Bootstrap.class.getResourceAsStream("/mconfig.xml");
        if (opt1 != null) {
            return opt1;
        }

        InputStream opt2 = Bootstrap.class.getResourceAsStream("/config.xml");
        return opt2;
    }
}

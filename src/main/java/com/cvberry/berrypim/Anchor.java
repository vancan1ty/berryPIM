package com.cvberry.berrypim;

import net.sf.saxon.xpath.XPathFactoryImpl;

/**
 * Created by vancan1ty on 1/3/2016.
 */
public class Anchor {
    private static Anchor ourInstance = new Anchor();

    public static Anchor getInstance() {
        return ourInstance;
    }

    private Anchor() {}

    private String appName;
    private String PIMFILESROOT;

    public String getAppName() {return appName;}
    public void setAppName(String appName) {this.appName = appName;}

    public String getPIMFilesRoot() {return PIMFILESROOT;}
    public void setPIMFilesRoot(String pimFilesRoot) {this.PIMFILESROOT = pimFilesRoot;}

    private Templater templater;

    public Templater getTemplater() {return templater;}
    public void setTemplater(Templater templater) {this.templater = templater;}

    private Dispatcher dispatcher;
    public Dispatcher getDispatcher() {return dispatcher;}
    public void setDispatcher(Dispatcher dispatcher) {this.dispatcher = dispatcher;}

    private DataFilesManager dataFilesManager;
    public DataFilesManager getDataFilesManager() {return dataFilesManager;}
    public void setDataFilesManager(DataFilesManager dataFilesManager) {this.dataFilesManager = dataFilesManager;}

    //[CB] set on servlet initialization
    //root path without ending slash -- e.g. "/berryPIM"
    private String rootPath;
    public String getRootPath() {return rootPath;}
    public void setRootPath(String rootPath) {this.rootPath = rootPath;}

    private ImageStreamer imStreamer;
    public ImageStreamer getImageStreamer() {return imStreamer;}
    public void setImageStreamer(ImageStreamer imStreamer) {this.imStreamer = imStreamer;}

    private String authRealm;
    public String getAuthRealm() {return authRealm;}
    public void setAuthRealm(String authRealm) {this.authRealm = authRealm;}

    private AuthenticationManager authManager;
    public AuthenticationManager getAuthManager() {return authManager;}
    public void setAuthManager(AuthenticationManager authManager) {this.authManager = authManager;}

    XPathFactoryImpl xpf;
    public XPathFactoryImpl getXPF() {return xpf;}
    public void setXPF(XPathFactoryImpl xpf) {this.xpf= xpf;}
}

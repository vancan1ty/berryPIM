package com.cvberry.berrypim;

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

}

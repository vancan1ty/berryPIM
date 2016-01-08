package com.cvberry;

import com.cvberry.berrypim.AuthenticationFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;
import java.io.File;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello Jersey+Jetty!
 * <p>
 * derived from https://nikolaygrozev.wordpress.com/2014/10/16/rest-with-embedded-jetty-and-jersey-in-a-single-jar-step-by-step/
 * <p>
 * make the thing totally extensible so you can pass in a hashmap mapping template keys to handlers in addition to the
 * defaults.  also, you can specify your own styles, templates, etc... while respecting "open-closed" principle.
 * <p>
 * the defaults allow you to link xml with xpath expressions with forms, tables, and charts.  with the extensible model,
 * you can develop your own components.
 *
 * CB:TODO verify that everything works both standalone and within big jetty server.
 */
public class App {
    public static void main(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.addFilter(AuthenticationFilter.class,"/*", EnumSet.of(DispatcherType.REQUEST));

        String SysJETTYPORT = System.getProperty("JETTYPORT");
        int port = 8080;
        System.out.println("SysJETTYPORT: " + SysJETTYPORT);
        if(SysJETTYPORT!=null && !SysJETTYPORT.isEmpty()) {
           port = Integer.parseInt(SysJETTYPORT);
        }
        Server jettyServer = new Server(port);
        jettyServer.setHandler(context);

        ClassLoader loader = App.class.getClassLoader();
        URL indexLoc = loader.getResource("static/index.html");
        System.out.println("URL");
        System.out.println(indexLoc.toExternalForm());
        System.out.println(indexLoc.getProtocol().toString());
        String almostBasePath = indexLoc.toExternalForm();
        //String basePath = "jar:file:/C:/Users/vancan1ty/Desktop/gitrepos/berryPIM/target/berry-pim-1.0-SNAPSHOT.jar!/";

        //here comes my hack to remove the last two segments in a way that works for both jar-based deploys and folder-based deploys
        Pattern lastTwo = Pattern.compile(".*/([^/]*/[^/]*$)");
        Matcher m = lastTwo.matcher(almostBasePath);
        m.find();
        String unwantedText = m.group(1);
        int endPos = almostBasePath.length()-unwantedText.length();
        String basePath = almostBasePath.substring(0,endPos);

        /*if(indexLoc.getProtocol().equals("jar")) {
            JarURLConnection connection = (JarURLConnection) indexLoc.openConnection();
            ppLoc = connection.getJarFileURL().toURI();
        } else {
            File locFile = new File(indexLoc.toURI());
            ppLoc = locFile.getParentFile().getParentFile().toURI();
        }*/

        System.out.println(basePath);
        org.eclipse.jetty.servlet.DefaultServlet staticServlet = new org.eclipse.jetty.servlet.DefaultServlet();
        ServletHolder holderPwd = new ServletHolder("default", staticServlet);
        holderPwd.setInitParameter("resourceBase",basePath);
        holderPwd.setInitParameter("dirAllowed","true");
        System.out.println("resource base");
        System.out.println(holderPwd.getInitParameter("resourceBase"));


        context.addServlet(holderPwd,"/static/*");

        ServletHolder myServlet = context.addServlet(
                com.cvberry.berrypim.HelloServlet.class,
                "/*");


        try {
            jettyServer.start();
            System.out.println("real path");
            System.out.println(staticServlet.getServletContext().getRealPath("/"));
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}


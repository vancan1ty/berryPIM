package com.cvberry.berrypim;
// Import required java libraries

import com.cvberry.util.ResourceLister;
import com.cvberry.util.Utility;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

/**
 * Created by vancan1ty on 1/2/2016.
 */
public class HelloServlet extends HttpServlet {

    /*private File contactsDoc;
    private File contactsSchema;
    private String contactsDocStr;*/
    //private String cachedTemplate;
    private ServletConfig servletConfig;
    private Anchor myAnchor;
    private Templater myTemplater;


    public void init(ServletConfig config) throws ServletException {
        // Do required initialization
        /*contactsDoc = new File(PIMFILESROOT + "/" + "contacts.xml");
        try {
            contactsDocStr = Utility.slurp(contactsDoc.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        contactsSchema = new File(PIMFILESROOT + "/" + "contacts_schema.xsd");
        StringBuilder vMessBuilder = new StringBuilder();
        try {
            boolean contactsValid = XMLValidator.validateDocument(contactsDoc, contactsSchema, vMessBuilder);
            if (!contactsValid) {
                System.err.println(vMessBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }*/

        this.servletConfig = config;
        ServletContext context = config.getServletContext();
        //InputStream resourceContent = context.getResourceAsStream("/WEB-INF/templates/maintemplate.html");
        //InputStream cachedTemplateStream = ClassLoader.getSystemResourceAsStream("/WEB-INF/template/maintemplate.html");
        //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //InputStream theFlow = classLoader.getResourceAsStream("templates/maintemplate.html");
        //cachedTemplate = Utility.convertStreamToString(theFlow);

//      Collection<String> resources = ResourceLister.getResources(Pattern.compile(".*"));
//      StringBuilder joined = new StringBuilder();
//      Iterator<String> strIterator = resources.iterator();
//      while(strIterator.hasNext()) {
//          String next = strIterator.next();
//          joined.append(next);
//          if(strIterator.hasNext()) {
//             joined.append(",");
//          }
//      }
//      cachedTemplate = joined.toString();

        //mainTemplate = new File(PIMFILESROOT+"/"+"contacts_schema.xsd");

        try {
            Bootstrap.bootstrap(context.getContextPath());
        } catch (Exception e) { //then setup did not succeed
            e.printStackTrace();
            System.exit(1);
        }

        myAnchor = Anchor.getInstance();
        myTemplater = myAnchor.getTemplater();

    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type
        response.setContentType("text/html");

        // Actual logic goes here.
        //PrintWriter out = response.getWriter();
        String templated = myTemplater.template(myTemplater.getMainTemplateContents());
        System.out.println("pathInfo");
        String fullReqStr = request.getPathInfo() + "?" + request.getQueryString();
        System.out.println(fullReqStr);
        myAnchor.getDispatcher().dispatch(fullReqStr,templated,request,response);
        //out.println(ResourceLister.getResources(Pattern.compile(".*\\.html")).stream().collect(Collectors.joining("\n")));
        //out.println("<textarea>");
        //out.println(contactsDocStr);
        //out.println("</textarea>");
    }

    public void destroy() {
        // do nothing.
    }
}

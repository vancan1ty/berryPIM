package com.cvberry.berrypim;
// Import required java libraries
import com.cvberry.util.Utility;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by vancan1ty on 1/2/2016.
 */
public class HelloServlet extends HttpServlet {

  private String message;
  private String PIMFILESROOT;
  private File contactsDoc;
  private File contactsSchema;
  private String contactsDocStr;

    public void init() throws ServletException
  {
      // Do required initialization
      message = "Hello World";
      String pimFilesRoot = System.getProperty("BERRYPIM_DATA_ROOT");
      if (pimFilesRoot == null) {
          PIMFILESROOT = "berryData";
      } else {
          PIMFILESROOT = pimFilesRoot;
      }

      contactsDoc = new File(PIMFILESROOT+"/"+"contacts.xml");
      try {
          contactsDocStr = Utility.slurp(contactsDoc.getPath());
      } catch (IOException e) {
          e.printStackTrace();
      }
      contactsSchema = new File(PIMFILESROOT+"/"+"contacts_schema.xsd");
      StringBuilder vMessBuilder = new StringBuilder();
      try {
          boolean contactsValid = XMLValidator.validateDocument(contactsDoc,contactsSchema,vMessBuilder);
          if(!contactsValid) {
              System.err.println(vMessBuilder.toString());
          }
      } catch (IOException e) {
          e.printStackTrace();
      } catch (ParserConfigurationException e) {
          e.printStackTrace();
      }
  }

  public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      // Set response content type
      response.setContentType("text/html");

      // Actual logic goes here.
      PrintWriter out = response.getWriter();
      out.println("<h1> Waddup Jackets </h1>");
      out.println("Working Directory = " +
              System.getProperty("user.dir"));
      out.println("<hr/>");
      out.println("<pre>");
      out.println(contactsDocStr);
      out.println("</pre>");
  }

  public void destroy()
  {
      // do nothing.
  }
}

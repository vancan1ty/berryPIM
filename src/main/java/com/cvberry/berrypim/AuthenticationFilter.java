package com.cvberry.berrypim;

import org.xml.sax.SAXException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by vancan1ty on 1/7/2016.
 *
 * In most browsers, you can log out either by sending an incorrect username:pass pass in the url or by using the
 * logout page.  In Chrome, however, this does not work properly.  Using the logout page, for example, the
 * browser temporarily changes its saved credentials to the new ones specified, but as soon as you try to go back
 * to the site, upon encountering a failed login, the browser reverts to the old (working) saved credentials. That
 * means, among other things, that it didn't really delete your credentials anyway.
 *
 * The above report comes from testing on "localhost", it may be different at an actual domain.
 */
public class AuthenticationFilter implements Filter {

    public static long lastFailedAttempt = 0; //we implement a throttle -- no more than 1 failed request every 2 seconds
    private AuthenticationManager authManager;
    private Anchor myAnchor;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            authManager = new AuthenticationManager(System.getProperty("BPIM_PWFILE"));
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        myAnchor = Anchor.getInstance();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest mRequest = (HttpServletRequest) request;
        HttpServletResponse mResponse = (HttpServletResponse) response;
        String userName = null;
        String password = null;
        Map.Entry<String,String>  creds = null;
        try {
            creds = credentialsWithBasicAuthentication(mRequest);
            if (creds == null) {
               throw new RuntimeException("no authorization info passed it seems.");
            }
            userName = creds.getKey();
            password = creds.getValue();
        } catch (Exception e) {//we need to request auth info.
            mResponse.setStatus(401);
            mResponse.setHeader("WWW-Authenticate","Basic realm=\""+myAnchor.getAuthRealm()+"\"");
            mResponse.getOutputStream().print("auth info needed.");
            return;
        }

        if (mRequest.getPathInfo().equals("/logout")) { //do special logout stuff
            if (authManager.authenticateUser(userName, password)) {
                mResponse.setStatus(401);
                mResponse.setHeader("WWW-Authenticate", "Basic realm=\"" + myAnchor.getAuthRealm() + "\"");
                mResponse.getOutputStream().print("You must input new credentials to overwrite the old in order to log out.");
            } else {//then we successfully logged out
                mResponse.setStatus(200);
                mResponse.setHeader("WWW-Authenticate", "Basic realm=\"" + myAnchor.getAuthRealm() + "\"");
                mResponse.getOutputStream().print("You have successfuly logged out!");
                return;
            }
        } else {
            if (authManager.authenticateUser(userName, password)) {
                request.setAttribute("creds", creds);
                chain.doFilter(request, response);
            } else { //authorization failed.
                long currTime = new Date().getTime();
                if (currTime - lastFailedAttempt < 2000) {//then fail the request.
                    mResponse.setStatus(403);
                    mResponse.getOutputStream().print("wait");
                } else {
                    mResponse.setStatus(401);
                    mResponse.setHeader("WWW-Authenticate", "Basic realm=\"" + myAnchor.getAuthRealm() + "\"");
                    mResponse.getOutputStream().print("auth info needed.");
                }
                lastFailedAttempt = currTime;

                return;
            }
        }
    }

    @Override
    public void destroy() {

    }

    //http://stackoverflow.com/questions/15611653/implementing-http-basic-authentication-in-a-servlet
    public Map.Entry<String,String> credentialsWithBasicAuthentication(HttpServletRequest req) {
        String authHeader = req.getHeader("Authorization");
        if (authHeader != null) {
            StringTokenizer st = new StringTokenizer(authHeader);
            if (st.hasMoreTokens()) {
                String basic = st.nextToken();

                if (basic.equalsIgnoreCase("Basic")) {
                    try {
                        String credentials = new String(Base64.getDecoder().decode(st.nextToken()), "UTF-8");
                        int p = credentials.indexOf(":");
                        if (p != -1) {
                            String login = credentials.substring(0, p).trim();
                            String password = credentials.substring(p + 1).trim();

                            return new AbstractMap.SimpleImmutableEntry<String, String>(login, password);
                        } else {
                            throw new RuntimeException("invalid auth token");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        throw new RuntimeException("invalid retrieve auth info.");
                    }
                }
            }
        }

        return null;
    }
}

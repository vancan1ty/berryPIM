package com.cvberry.berrypim;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by vancan1ty on 1/7/2016.
 */
public class AuthenticationFilter implements Filter {

    public static long lastFailedAttempt = 0; //we implement a throttle -- no more than 1 failed request every 2 seconds

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
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
            mResponse.setHeader("WWW-Authenticate","Basic realm=\"org.cvberry.com\"");
            mResponse.getOutputStream().print("auth info needed.");
            return;
        }

        if(userName.equals("vancan1ty") && password.equals("446730842808447096757")) {
            request.setAttribute("creds",creds);
            chain.doFilter(request,response);
        } else { //authorization failed.
            long currTime = new Date().getTime();
            if(currTime-lastFailedAttempt<2000) {//then fail the request.
                mResponse.setStatus(403);
                mResponse.getOutputStream().print("wait");
            } else {
                mResponse.setStatus(401);
                mResponse.setHeader("WWW-Authenticate","Basic realm=\"org.cvberry.com\"");
                mResponse.getOutputStream().print("auth info needed.");
            }
            lastFailedAttempt = currTime;

            return;
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

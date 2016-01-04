package com.cvberry.berrypim;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by vancan1ty on 1/3/2016.
 */
public class Dispatcher {
    public static Map<String, ControllerObject> dispatchMap;

    public static void dispatch(String pathStr, String mainTemplate, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] pathComponents = getPathComponents(pathStr);
        ControllerObject controller = null;
        for (int i = 0; i < pathComponents.length; i++) {
            String toLook = "";
            for (int j = 0; j<i; j++) {
                toLook = toLook+pathComponents[j]+"/";
            }
            toLook = toLook+pathComponents[i];
            if(dispatchMap.containsKey(toLook)) {
               controller = dispatchMap.get(toLook);
                break;
            }
        }
        if(controller == null) {
            response.sendError(404,"couldn't find a page corresponding to that url.");
            return;
        }
        String myOutput = controller.control(getPathComponents(pathStr),getQueryStr(pathStr),mainTemplate);
        response.getOutputStream().print(myOutput);
    }

    public static String[] getPathComponents(String pathStr) {
        String[] twoParts = pathStr.split("\\?");
        return twoParts[0].toLowerCase().split("/");
    }

    public static String getQueryStr(String pathStr) {
        String[] twoParts = pathStr.split("\\?");
        return twoParts[1];
    }
}

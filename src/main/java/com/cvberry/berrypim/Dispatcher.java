package com.cvberry.berrypim;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vancan1ty on 1/3/2016.
 */
public class Dispatcher {

    private Anchor myAnchor;
    private Map<String, ControllerObject> dispatchMap;

    public Dispatcher() {
        this.dispatchMap = new HashMap<>();
        myAnchor = Anchor.getInstance();
    }

    public void dispatch(String pathStr, String mainTemplate, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (pathStr.startsWith("/")) {
            pathStr = pathStr.substring(1);
        }

        if (pathStr.endsWith(".png")) {
            response.setStatus(200);
            myAnchor.getImageStreamer().getImage(pathStr, response);
            return;
        }

        String[] pathComponents = getPathComponents(pathStr);
        ControllerObject controller = null;
        for (int i = 0; i < pathComponents.length; i++) {
            String toLook = "";
            for (int j = 0; j < i; j++) {
                toLook = toLook + pathComponents[j] + "/";
            }
            toLook = toLook + pathComponents[i];
            if (dispatchMap.containsKey(toLook)) {
                controller = dispatchMap.get(toLook);
                break;
            }
        }
        if (controller == null) {
            response.sendError(404, "couldn't find a page corresponding to that url.");
            return;
        }
        String myOutput = controller.control(getPathComponents(pathStr), getQueryStr(pathStr), mainTemplate);
        response.getOutputStream().print(myOutput);
    }

    public void addDispatchPath(String path, ControllerObject controller) {
        this.dispatchMap.put(path.toLowerCase(), controller);
    }

    public static String[] getPathComponents(String pathStr) {
        String[] twoParts = pathStr.split("\\?");
        return twoParts[0].toLowerCase().split("/");
    }

    public static String getQueryStr(String pathStr) {
        String[] twoParts = pathStr.split("\\?");
        if (twoParts.length < 2) {
            return null;
        } else {
            try {
                return URLDecoder.decode(twoParts[1].replace("+", "%2B"), "UTF-8").replace("%2B", "+");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null; //should never get here.
    }
}

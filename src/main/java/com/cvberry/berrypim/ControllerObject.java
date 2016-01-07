package com.cvberry.berrypim;

import java.util.Map;

/**
 * Created by vancan1ty on 1/3/2016.
 */
public interface ControllerObject {
    public String control(String[] pathComponents, Map<String, String[]> queryParams, String template, String dataBody);
}

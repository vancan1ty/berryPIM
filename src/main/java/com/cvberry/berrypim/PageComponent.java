package com.cvberry.berrypim;

import com.cvberry.util.AuthInfoHolder;

import java.util.Map;

/**
 * Created by vancan1ty on 1/18/2016.
 */
public interface PageComponent {
    public String makeContentPaneHTML(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                      AuthInfoHolder authInfo) throws Exception;
}

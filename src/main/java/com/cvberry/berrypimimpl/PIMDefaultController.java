package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.ControllerObject;
import com.cvberry.berrypim.DefaultController;
import com.cvberry.util.Utility;

import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class PIMDefaultController extends DefaultController implements ControllerObject {

    @Override
    public List<Map.Entry<String, String>> getLTabItems() {
        String[] starter = {
                "raw", "Raw",
                "dashboard", "Dashboard",
                "calendar", "Calendar",
                "contacts", "Contacts",
                "notes", "Notes",
                "todos", "Todos",
                "finance", "Finance"
        };
        return Utility.tupleizeArray(starter);
    }

    @Override
    public String getDefaultTab() {
       return "dashboard";
    }

}

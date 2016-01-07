package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.ControllerObject;
import com.cvberry.util.Utility;

import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class NotesController extends RawController implements ControllerObject {

    public NotesController(String controllerBase) {
        super(controllerBase);
        UNIVERSALFILENAME = "essaysdoc.org";
    }

    @Override
    public List<Map.Entry<String, String>> getTopTabsItems() {
        String[] starter = {
                "home", "Notes Home",
        };
        return Utility.tupleizeArray(starter);
    }

}

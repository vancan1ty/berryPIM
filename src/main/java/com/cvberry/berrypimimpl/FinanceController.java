package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.ControllerObject;
import com.cvberry.util.Utility;

import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class FinanceController extends RawController implements ControllerObject {

    public FinanceController(String controllerBase) {
        super(controllerBase);
        UNIVERSALFILENAME = "finance.xml";
    }

    @Override
    public List<Map.Entry<String, String>> getTopTabsItems() {
        String[] starter = {
                "finance", "Finance Home",
        };
        return Utility.tupleizeArray(starter);
    }

}

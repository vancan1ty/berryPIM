package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.ControllerObject;
import com.cvberry.berrypim.widgets.PieChart;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.Utility;
import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class DashboardController extends PIMDefaultController implements ControllerObject {

    public DashboardController(String controllerBase) {
        this.controllerBase = controllerBase;
    }

    @Override
    public List<Map.Entry<String, String>> getTopTabsItems() {
        String[] starter = {
                "home", "Home",
        };
        return Utility.tupleizeArray(starter);
    }

    @Override
    public String fill_contentPane(String[] pathComponents, Map<String, String[]> queryParams, String dataBody,
                                   AuthInfoHolder authInfo) {
        StringBuilder out = new StringBuilder();
        out.append("<p>Welcome to berryPIM.  This program is still a work in progress!</p>");
        out.append("<h2>System Statistics</h2>");
        OperatingSystemMXBean opBean= (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long totalMemorySize = opBean.getTotalPhysicalMemorySize();
        long freeMemory = opBean.getFreePhysicalMemorySize();
        long usedMemory = totalMemorySize - freeMemory;

        Double BYTESINGIG = 1000000000.0;
        File file = new File("/");
        long totalDiskSize = (file.getTotalSpace());
        long freeDiskSpace = file.getFreeSpace();
        long usedDiskSpace = totalDiskSize-freeDiskSpace;
        out.append("<table class='fullspan center'><tr>\n");
        out.append("<th><h3>Memory</h3></th>\n");
        out.append("<th><h3>Disk</h3></th>\n");
        out.append("</tr><tr>\n");
        out.append("<td>");
        out.append(PieChart.createAndEnqueueChart(
                PieChart.createDatasetFromArrays(new String[]{"Free","Used"},
                        new Double[]{freeMemory/BYTESINGIG,usedMemory/BYTESINGIG}),
                null,rootPathStr,false,true,300,300));
        out.append("<br><table>\n");
        out.append("<tr><th>Total</th><th>Free</th><th>Used</th></tr>\n");
        out.append(String.format("<tr><td>%.1f</td><td>%.1f</td><td>%.1f</td></tr>\n",
                totalMemorySize/BYTESINGIG,freeMemory/BYTESINGIG,usedMemory/BYTESINGIG));
        out.append("</table>\n");
        out.append("</td><td>");
        out.append(PieChart.createAndEnqueueChart(
                PieChart.createDatasetFromArrays(new String[]{"Free","Used"},
                        new Double[]{freeDiskSpace/BYTESINGIG,usedDiskSpace/BYTESINGIG}),
                null,rootPathStr,false,true,300,300));
        out.append("<br><table>\n");
        out.append("<tr><th>Total</th><th>Free</th><th>Used</th></tr>\n");
        out.append(String.format("<tr><td>%.1f</td><td>%.1f</td><td>%.1f</td></tr>\n",
                totalDiskSize/BYTESINGIG,freeDiskSpace/BYTESINGIG,usedDiskSpace/BYTESINGIG));
        out.append("</td>");
        out.append("</table>\n");


        return out.toString();
    }
}

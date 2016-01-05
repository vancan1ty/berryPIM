package com.cvberry.berrypim;

import com.cvberry.util.Utility;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class DataFilesManager {

    private Map<String,String> fileContentsMap;

    public DataFilesManager(String filesRoot) throws IOException {
        fileContentsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        File rootFile = new File(filesRoot);
        File[] dataFilesAndFolders = rootFile.listFiles();
        for (File f : dataFilesAndFolders) {
            if(f.isFile()) {
                String contents = Utility.slurp(f.getPath());
                fileContentsMap.put(f.getName(),contents);
            }
        }
    }

    public void updateFile(String fileName, String contents) {
        //CB TODO implement
    }

    public String getFileContents(String fileName) {
        return fileContentsMap.get(fileName);
    }

    public List<String> listDataFiles() {
        List<String> fileList = new ArrayList<>(fileContentsMap.keySet());
        Collections.sort(fileList);
        return fileList;
    }
}

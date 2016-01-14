package com.cvberry.berrypim;

import com.cvberry.util.Utility;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.*;
import java.util.*;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class DataFilesManager {

    private Map<String, FileInfoObj> fileContentsMap;
    private File rootFile;
    public GitManager gitManager;

    public DataFilesManager(String filesRoot) throws IOException {
        rootFile = new File(filesRoot);
        readInAllFiles();
        gitManager = new GitManager(filesRoot);
    }

    public boolean readInAllFilesSafe(StringBuilder toWriteTo) {
        try {
            readInAllFiles();
        } catch (IOException e) {
            e.printStackTrace();
            toWriteTo.append(e.getMessage());
            return false;
        }
        return true;
    }

    public void readInAllFiles() throws IOException {
        fileContentsMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        File[] dataFilesAndFolders = rootFile.listFiles();
        for (File f : dataFilesAndFolders) {
            if (f.isFile()) {
                String contents = Utility.slurp(f.getPath());
                long lastModified = f.lastModified();
                FileInfoObj fileObj = new FileInfoObj(f, lastModified, contents);
                fileContentsMap.put(f.getName(), fileObj);
            }
        }
    }


    /**
     * @param fileName
     * @param toWriteTo can be null
     * @return
     */
    public boolean refreshFileContents(String fileName, StringBuilder toWriteTo) {
        FileInfoObj fileObj = this.fileContentsMap.get(fileName);
        if (fileObj == null) {
            String probStr = "file '" + fileName + "' not found in app's records!";
            if (toWriteTo != null) {
                toWriteTo.append(probStr);
            }
            return false;
        }

        String newContents = null;
        try {
            newContents = Utility.slurp(fileObj.actualFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            if (toWriteTo != null) {
                toWriteTo.append(e.getMessage());
            }
            return false;
        }

        //update values in place
        fileObj.fileContents = newContents;
        fileObj.lastModifiedTime = fileObj.actualFile.lastModified();

        if (toWriteTo != null) {
            toWriteTo.append("The contents of " + fileName + " have been refreshed.");
        }

        return true;
    }

    /**
     * @param fileName
     * @param dataBody
     * @param toWriteTo
     * @return whether or not the save was successful
     */
    public boolean saveNewContentsToFile(String fileName, String dataBody, StringBuilder toWriteTo,
                                         boolean overrideModification, boolean doGitCommit) throws IOException, InterruptedException {
        boolean fileModified = false;
        try {
            fileModified = this.getHasFileBeenModified(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toWriteTo.append(e.getMessage());
            return false;
        }

        if (fileModified) {
            if (overrideModification) {//keep going

            } else { //abort
                toWriteTo.append("File has been modified!  Please refresh your file contents, " +
                        "make your edits, then try saving again.");
                return false;
            }
        }

        FileInfoObj fileObj = this.fileContentsMap.get(fileName);
        File actualFile = null;
        if (fileObj == null) {
            //then we have to make a new one
            String newFilePath = rootFile.getPath() + File.separator + fileName;
            File newFile = new File(newFilePath);
            actualFile = newFile;
        } else {
            actualFile = fileObj.actualFile;
        }

        try {
            Utility.spit(actualFile.getPath(), dataBody);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            toWriteTo.append(e.getMessage());
            return false;
        }

        if (fileObj == null) {
            fileObj = new FileInfoObj(actualFile, actualFile.lastModified(), dataBody);
            this.fileContentsMap.put(fileName,fileObj);
        }

        toWriteTo.append(fileName + " has been saved.");

        if(doGitCommit) {//then use GitManager to commit our changes.
            try {
                gitManager.addCommitFile(fileName);//CB NOTE limitation -- only supports top-level files I think
            } catch (GitAPIException e) {
                e.printStackTrace();
                toWriteTo.append(Utility.collectExceptionToString(e));
            }
        }
        return true;
    }

    public boolean getHasFileBeenModified(String fileName) throws FileNotFoundException {
        FileInfoObj obj = fileContentsMap.get(fileName);
        if (obj == null) {
            return false; //clearly not modified, as it doesn't exist.
            //throw new FileNotFoundException("file '" + fileName + "' not found in app's records!");
        }
        long origLMTime = obj.lastModifiedTime;
        long newLMTime = obj.actualFile.lastModified();
        if (newLMTime == origLMTime) {
            return false;
        } else {
            return true;
        }
    }

    public String getFileContents(String fileName) {
        FileInfoObj obj = fileContentsMap.get(fileName);
        if (obj != null) {
            return obj.fileContents;
        } else {
            return null;
        }
    }

    public List<String> listDataFiles() {
        List<String> fileList = new ArrayList<>(fileContentsMap.keySet());
        List<String> outFileList = new ArrayList<>();
        for (String s : fileList) {
            if (!s.endsWith(".bPIMD")) {
                outFileList.add(s);
            }
        }
        Collections.sort(outFileList);
        return outFileList;
    }

     public List<String> listAllFiles() {
        List<String> fileList = new ArrayList<>(fileContentsMap.keySet());
        Collections.sort(fileList);
        return fileList;
    }

    public static class FileInfoObj {
        File actualFile;
        long lastModifiedTime;
        String fileContents;

        public FileInfoObj(File actualFile, long lastModifiedTime, String fileContents) {
            if (actualFile == null || fileContents == null) {
                throw new RuntimeException("value can't be null here!");
            }
            this.actualFile = actualFile;
            this.lastModifiedTime = lastModifiedTime;
            this.fileContents = fileContents;
        }

        //autogenerated
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FileInfoObj that = (FileInfoObj) o;

            if (lastModifiedTime != that.lastModifiedTime) return false;
            if (!actualFile.equals(that.actualFile)) return false;
            return fileContents.equals(that.fileContents);

        }

        //autogenerated
        @Override
        public int hashCode() {
            int result = actualFile.hashCode();
            result = 31 * result + (int) (lastModifiedTime ^ (lastModifiedTime >>> 32));
            result = 31 * result + fileContents.hashCode();
            return result;
        }
    }
}

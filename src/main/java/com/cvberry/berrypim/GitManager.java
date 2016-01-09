package com.cvberry.berrypim;

import com.cvberry.util.Utility;

import java.io.*;
import java.util.Map;

/**
 * Created by vancan1ty on 1/7/2016.
 */
public class GitManager {

    /**
     * commits
     */
    public boolean syncToGit(String rootPath, StringBuilder toWriteTo) throws IOException, InterruptedException {
        //should have already saved the work.

        //1. add EVERYTHING in the data directory.
        boolean r1 = runShellCommandWrapped(rootPath, "git add --all .",toWriteTo);

        //2. check if there are changes in the index after the previous step, if there are, run a commit.
        //http://stackoverflow.com/questions/3878624/how-do-i-programmatically-determine-if-there-are-uncommited-changes
        boolean stagedChanges = !runShellCommandWrapped(rootPath,
                "git diff-index --cached --quiet HEAD --ignore-submodules --",toWriteTo);

        boolean r2 = true;
        if(stagedChanges) {//then run a commit.
            r2 = runShellCommandWrapped(rootPath, "git commit -m 'berrypim_data_commit'",toWriteTo);
        }

        //3. git pull
        boolean r3 = runShellCommandWrapped(rootPath, "git pull origin master'",toWriteTo);

        return false;
    }

    /**
     * @param toWriteTo can be null
     * @return whether or not the operation was successful.
     */
    public static boolean gitPull(String rootPath, StringBuilder toWriteTo) throws IOException, InterruptedException {
        String command = "git pull origin master";
        return runShellCommandWrapped(rootPath,command,toWriteTo);
    }


    public static boolean gitStatus(String rootPath, StringBuilder toWriteTo) throws IOException, InterruptedException {
        String command = "git status";
        return runShellCommandWrapped(rootPath,command,toWriteTo);
    }

    public static boolean gitPull(String rootPath, StringBuilder toWriteTo, String password)
            throws IOException, InterruptedException {
        String command = "git pull origin master";
        return runShellCommandWrapped(rootPath,command,toWriteTo,password);
    }

    /**
     * @param file
     * @param toWriteTo can be null
     * @return whether or not the operation was successful.
     */
    public static boolean addCommitFile(String rootPath, File file, StringBuilder toWriteTo) throws IOException, InterruptedException {
        String command = "git add " + file.getName();
        return runShellCommandWrapped(rootPath,command,toWriteTo);
    }

    public static boolean runShellCommandWrapped(String rootPath, String command, StringBuilder toWriteTo) throws IOException, InterruptedException {
       return runShellCommandWrapped(rootPath,command,toWriteTo,null);
    }
    /**
     * @param toWriteTo can be null
     * @return whether or not the operation was successful.
     */
    public static boolean runShellCommandWrapped(String rootPath, String command, StringBuilder toWriteTo, String toWriteOut)
            throws IOException, InterruptedException {
        String[] commandSplit = command.split(" "); //CB TODO support spaces in filenames.

        int status = executeShellCommandsWriteOutput(new File(rootPath),commandSplit,toWriteTo,toWriteOut);
        if (status != 0) {
            return false;
        } else {
            return true;
        }
    }

    public static int executeShellCommandsWriteOutput(File homeDir, String[] segments, StringBuilder toWriteTo,
                                                      String toPassToInput)
            throws IOException, InterruptedException {
        StringBuilder nNullStrBuilder = toWriteTo;
        if(toWriteTo == null) {
            nNullStrBuilder = new StringBuilder();
        }

        ProcessBuilder pb = new ProcessBuilder(segments);
            pb.directory(homeDir);
        Process p = pb.start();
        if(toPassToInput != null) {
            OutputStream outS = p.getOutputStream();
            outS.write(toPassToInput.getBytes("UTF-8"));
            outS.flush();
            outS.close();
        }

        p.waitFor();
        int out = p.exitValue();
        String stdOut = Utility.convertStreamToString(p.getInputStream());
        String stdErr = Utility.convertStreamToString(p.getErrorStream());
        nNullStrBuilder.append("output\n" + stdOut +"\n");
        nNullStrBuilder.append("error\n"+ stdErr + "\n");
        return out;
    }



}

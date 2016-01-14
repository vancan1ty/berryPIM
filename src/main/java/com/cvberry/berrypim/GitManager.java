package com.cvberry.berrypim;

import com.cvberry.util.Utility;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.*;

import java.io.*;
import java.util.Map;
import java.util.Set;

/**
 * Created by vancan1ty on 1/7/2016.
 */
public class GitManager {

    //localPath = "/home/me/repos/mytest";
    //remotePath = "git@github.com:me/mytestrepo.git";
    private String localPath, remotePath;
    private Repository localRepo;
    private Git git;//whenever you get a reference to this, you must synchronize on your GitManager instance.

    public GitManager(String localPath) throws IOException {
        this.localPath = localPath;
        localRepo = new FileRepository(localPath + "/.git");
        git = new Git(localRepo);
    }

//    public void testCreate() throws IOException {
//        Repository newRepo = new FileRepository(localPath + ".git");
//        newRepo.create();
//    }

//    public void testClone() throws IOException, GitAPIException {
//        Git.cloneRepository().setURI(remotePath)
//                .setDirectory(new File(localPath)).call();
//    }

    public synchronized void addAllFiles() throws IOException, GitAPIException {
        File myfile = new File(localPath + "/");
        myfile.createNewFile();
        git.add().addFilepattern("").call();
    }

//    public void testTrackMaster() throws IOException, JGitInternalException,
//            GitAPIException {
//        git.branchCreate().setName("master")
//                .setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM)
//                .setStartPoint("origin/master").setForce(true).call();
//    }

    public synchronized void testPull() throws IOException, GitAPIException {
        git.pull().call();
    }

    public synchronized void addCommitFile(String filePath) throws GitAPIException {
        //1. Stage all files in the repo including new files
        git.add().addFilepattern(filePath).call();

        Status status = git.status().call();
        //2. check if there are uncommited changes
        Set<String> uncommittedChanges = status.getUncommittedChanges();
        if (!uncommittedChanges.isEmpty()) {
            //3. then commit the changes.
            git.commit()
                    .setMessage("berrypim_data_commit")
                    .call();
        }
    }

    //https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/CommitAll.java
    public synchronized void syncToGit(String password) throws IOException, GitAPIException {

        //1. Stage all files in the repo including new files
        git.add().addFilepattern(".").call();

        //2.  check if there are uncommited files after the previous step.  if there are, run a commit.
        Status status = git.status().call();
        Set<String> uncommittedChanges = status.getUncommittedChanges();
        if (!uncommittedChanges.isEmpty()) {
            //3. then commit the changes.
            git.commit()
                    .setMessage("berrypim_data_commit")
                    .call();
        }

        //3. do git git pull
        doGitPull(password);

        //4. do git git push
        doGitPush(password);
    }

    public synchronized void doGitPull(final String password) throws GitAPIException {
                final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setPassword(password);
            }
        };

        PullCommand pullCmd = git.pull();
        pullCmd.setRemote("origin");
        pullCmd.setTransportConfigCallback( new TransportConfigCallback() {
            @Override
            public void configure( Transport transport ) {
                SshTransport sshTransport = (SshTransport)transport;
                sshTransport.setSshSessionFactory( sshSessionFactory );
            }
        } );
        pullCmd.call();
    }

    public synchronized void doGitPush(final String password) throws GitAPIException {
                final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
                session.setPassword(password);
            }
        };

        PushCommand pushCmd = git.push();
        pushCmd.setRemote("origin");
        pushCmd.setTransportConfigCallback( new TransportConfigCallback() {
            @Override
            public void configure( Transport transport ) {
                SshTransport sshTransport = (SshTransport)transport;
                sshTransport.setSshSessionFactory( sshSessionFactory );
            }
        } );
        pushCmd.call();
    }

    /**
     * commits
     */
//    public boolean syncToGit(String rootPath, StringBuilder toWriteTo) throws IOException, InterruptedException {
//        //should have already saved the work.
//
//        //1. add EVERYTHING in the data directory.
//        boolean r1 = runShellCommandWrapped(rootPath, "git add --all .",toWriteTo);
//
//        //2. check if there are changes in the index after the previous step, if there are, run a commit.
//        //http://stackoverflow.com/questions/3878624/how-do-i-programmatically-determine-if-there-are-uncommited-changes
//        boolean stagedChanges = !runShellCommandWrapped(rootPath,
//                "git diff-index --cached --quiet HEAD --ignore-submodules --",toWriteTo);
//
//        boolean r2 = true;
//        if(stagedChanges) {//then run a commit.
//            r2 = runShellCommandWrapped(rootPath, "git commit -m 'berrypim_data_commit'",toWriteTo);
//        }
//
//        //3. git pull
//        boolean r3 = runShellCommandWrapped(rootPath, "git pull origin master'",toWriteTo);
//
//        return false;
//    }

    public Git getGit() {
        return git;
    }

}

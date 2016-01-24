package com.cvberry;

import com.cvberry.berrypim.AuthenticationManager;
import com.cvberry.berrypim.Bootstrap;
import com.cvberry.berrypim.GitManager;
import com.cvberry.util.AuthInfoHolder;
import com.cvberry.util.PasswordHasherLite;
import com.cvberry.util.Utility;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.transport.*;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * I usually primarily test the business logic layer.
 */
public class AppTest {

//	@Test
//    public void testNextPrime()
//    {
//        assertEquals(Model.findNextBiggerPrime(3),5);
//        assertEquals(Model.findNextBiggerPrime(1),2);
//        assertEquals(Model.findNextBiggerPrime(960),967);
//    }

    @Test
    public void testBootstrap() throws SAXException, ParserConfigurationException, XPathExpressionException, IOException,
            IllegalAccessException, InstantiationException, ClassNotFoundException, URISyntaxException, XPathFactoryConfigurationException {
        Bootstrap.bootstrap("/berryPIM", "../berryData");
    }

    static String pw1 = "!thebuilder123!";
    static String wpw1 = "hoax22";

    @Test
    public void testPasswordHasherLite() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        PasswordHasherLite liteHasher = new PasswordHasherLite();

        byte[] pw1Bytes = pw1.getBytes("UTF-8");
        byte[] wpw1Bytes = wpw1.getBytes("UTF-8");
        String hashedStr = liteHasher.createUsernameSaltHashStr("bob", pw1Bytes);

        PasswordHasherLite liteHasher2 = new PasswordHasherLite();
        AuthInfoHolder authInfo = PasswordHasherLite.retrieveSaltAndHash(hashedStr);
        assertTrue("password hasher properly recognizes correct password", liteHasher2.validatePassword(pw1Bytes, authInfo.salt, authInfo.hash));
        assertFalse("password hasher properly recognizes incorrect password", liteHasher2.validatePassword(wpw1Bytes, authInfo.salt, authInfo.hash));
    }

    @Test
    public void testAuthManager() throws IOException, NoSuchAlgorithmException {
        AuthenticationManager myManager = new AuthenticationManager("berrypim_test_passwords.txt");
        assertTrue(myManager.authenticateUser("vancan1ty", "coolpassword"));
    }

    @Test
    public void testGitAddAll() throws IOException, GitAPIException {
        GitManager manager = new GitManager("../berryData");
        synchronized (manager) {
            manager.getGit().add().addFilepattern(".").call();
        }
    }

    // https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/porcelain/ListUncommittedChanges.java
    @Test
    public void testGitStatusStuff() throws IOException, GitAPIException {
        GitManager manager = new GitManager("../berryData");
        synchronized (manager) {
            Git git = manager.getGit();
            Status status = git.status().call();
            Set<String> conflicting = status.getConflicting();
            for (String conflict : conflicting) {
                System.out.println("Conflicting: " + conflict);
            }
            Set<String> added = status.getAdded();
            for (String add : added) {
                System.out.println("Added: " + add);
            }
            Set<String> changed = status.getChanged();
            for (String change : changed) {
                System.out.println("Change: " + change);
            }
            Set<String> missing = status.getMissing();
            for (String miss : missing) {
                System.out.println("Missing: " + miss);
            }
            Set<String> modified = status.getModified();
            for (String modify : modified) {
                System.out.println("Modification: " + modify);
            }
            Set<String> removed = status.getRemoved();
            for (String remove : removed) {
                System.out.println("Removed: " + remove);
            }
            Set<String> uncommittedChanges = status.getUncommittedChanges();
            for (String uncommitted : uncommittedChanges) {
                System.out.println("Uncommitted: " + uncommitted);
            }
            Set<String> untracked = status.getUntracked();
            for (String untrack : untracked) {
                System.out.println("Untracked: " + untrack);
            }
            Set<String> untrackedFolders = status.getUntrackedFolders();
            for (String untrack : untrackedFolders) {
                System.out.println("Untracked Folder: " + untrack);
            }
            Map<String, IndexDiff.StageState> conflictingStageState = status.getConflictingStageState();
            for (Map.Entry<String, IndexDiff.StageState> entry : conflictingStageState.entrySet()) {
                System.out.println("ConflictingState: " + entry);
            }
        }
    }

    @Test
    public void testURLDecoder() throws UnsupportedEncodingException {
        String trueDecoded ="//transaction[@amount>30]";
        //String uEncoded = URLEncoder.encode(trueDecoded,"UTF-8");
        String coded = "//transaction[@amount&gt;30]";
        //assertEquals(uEncoded,coded);
        String decoded = Utility.realDecode(coded);
        URLDecoder dec;
        assertEquals("simple decode test",trueDecoded,decoded);
    }


//    @Test
//    public void testGitPull() throws IOException, GitAPIException {
//        GitManager manager = new GitManager("../berryData");
//        Git git = manager.git;
//
//        final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
//            @Override
//            protected void configure(OpenSshConfig.Host host, Session session) {
//                session.setPassword("");
//            }
//        };
//
//        PullCommand pullCmd = git.pull();
//        pullCmd.setRemote("origin");
//        pullCmd.setTransportConfigCallback( new TransportConfigCallback() {
//            @Override
//            public void configure( Transport transport ) {
//                SshTransport sshTransport = ( SshTransport )transport;
//                sshTransport.setSshSessionFactory( sshSessionFactory );
//            }
//        } );
//        pullCmd.call();
//    }

//    @Test
//    public void testGitStatus() throws IOException, InterruptedException {
//        StringBuilder output = new StringBuilder();
//        GitManager.gitStatus("../berryData",output);
//        System.out.println(output.toString());
//        assertNotSame("can't be empty",output.toString(),"");
//    }


}

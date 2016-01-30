package com.cvberry;

import com.cvberry.berrypim.Anchor;
import com.cvberry.berrypim.AuthenticationManager;
import com.cvberry.berrypim.Bootstrap;
import com.cvberry.berrypim.GitManager;
import com.cvberry.berrypim.calendar.EffectiveCalendarGenerator;
import com.cvberry.util.*;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.transport.*;
import org.junit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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



//    @Test
//    public void getResourceListing() throws URISyntaxException, IOException {
//        String[] tempFileNames = ResourceLister.getResourceListingForPath(Anchor.class,"/templates");
//        for (String s : tempFileNames) {
//            System.out.println(s);
//        }
//    }

    @Test
    public void testTestCalExists() {
        assertNotNull("Test file missing",
                getClass().getResource("/testcal.xml"));
    }

    @Test
    public void testEffectiveCalendar() throws IOException, SAXException, ParserConfigurationException, DatatypeConfigurationException, TransformerException {
        InputStream is = this.getClass().getResourceAsStream("/testcal.xml");
        Document testCal = Utility.parseStreamToDOM(is);
        DomVisitingHelper.ItemVisitorHelper vHelper = new EffectiveCalendarGenerator.ItemRecurrenceHandler(testCal);
        int doCount = DomVisitingHelper.domVisitTree(testCal, vHelper);
        assertEquals(6,doCount);
        vHelper.doFinalAction();
        System.out.println(Utility.nodeToString(testCal.getFirstChild()));

    }

    @Test
    public void testDateParse() throws ParseException {
        SimpleDateFormat df = Utility.isoDateFormatter;
        Date d1 = df.parse("2016-01-07T00:00:00");
        Date d2 = df.parse("2012-09-04T12:00:00");
    }

    @Test
    public void testGenerateDatesForRecurrencePattern1() throws ParseException {
        EffectiveCalendarGenerator.RecurrenceParameters recParams1 =
                new EffectiveCalendarGenerator.RecurrenceParameters(
                        "DAILY",6,2,null,null,null,null,null,null,null,null,null,null
                );
        SimpleDateFormat df = Utility.isoDateFormatter;
        Date d1 = df.parse("2016-01-07T00:00:00");
        Collection<Date> dates = EffectiveCalendarGenerator.generateDatesForRecurrencePattern(d1,recParams1);
        assertEquals(6,dates.size());
        for (Date d : dates) {
            System.out.println(df.format(d));
        }
    }

    @Test
    public void testGenerateDatesForRecurrencePattern2() throws Exception {
        SimpleDateFormat df = Utility.isoDateFormatter;
        Date d1 = df.parse("2016-01-07T08:00:00");
        Date d2 = df.parse("2016-02-07T08:00:00");
        EffectiveCalendarGenerator.RecurrenceParameters recParams1 =
                new EffectiveCalendarGenerator.RecurrenceParameters(
                        "WEEKLY",null,null,d2,new String[]{"TU","TH"},null,null,null,null,null,null,null,null
                );
        Collection<Date> dates = EffectiveCalendarGenerator.generateDatesForRecurrencePattern(d1,recParams1);
        assertEquals(10,dates.size());
        for (Date d : dates) {
            System.out.println(df.format(d));
        }

        InputStream is = this.getClass().getResourceAsStream("/testcal.xml");
        Document testCal = Utility.parseStreamToDOM(is);
        Node event = Utility.runXPathOnDOMNode(testCal.getFirstChild(),"//vevent[@id='testrec2']").item(0);
        DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();
        EffectiveCalendarGenerator.SimpleEventFields fields =
                EffectiveCalendarGenerator.getSimpleEventFieldsFromVEvent(dataTypeFactory,event);
        Node recur = Utility.runXPathOnDOMNode(event,"properties/rrule/recur").item(0);
        Collection<Date> dates2 =  EffectiveCalendarGenerator.generateDatesForRecurrencePattern(fields.dateTime,recur);
        assertEquals(dates.size(),dates2.size());
        List<Date> d1List = new ArrayList<>(dates);
        List<Date> d2List = new ArrayList<>(dates2);
        for(int i = 0; i < d1List.size(); i++) {
            assertEquals(d1List.get(i),d2List.get(i));
        }
    }

    @Test
    public void testGenerateDatesForRecurrencePattern3() throws Exception {
        SimpleDateFormat df = Utility.isoDateFormatter;
        InputStream is = this.getClass().getResourceAsStream("/testcal.xml");
        Document testCal = Utility.parseStreamToDOM(is);
        Node event = Utility.runXPathOnDOMNode(testCal.getFirstChild(),"//vevent[@id='testrec3']").item(0);
        DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();
        EffectiveCalendarGenerator.SimpleEventFields fields =
                EffectiveCalendarGenerator.getSimpleEventFieldsFromVEvent(dataTypeFactory,event);
        Node recur = Utility.runXPathOnDOMNode(event,"properties/rrule/recur").item(0);
        Collection<Date> dates =  EffectiveCalendarGenerator.generateDatesForRecurrencePattern(fields.dateTime,recur);
        List<Date> d1List = new ArrayList<>(dates);
        for(int i = 0; i < d1List.size(); i++) {
            System.out.println(df.format(d1List.get(i)));
        }
    }

}

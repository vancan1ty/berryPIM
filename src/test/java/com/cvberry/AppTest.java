package com.cvberry;

import com.cvberry.berrypim.AuthenticationManager;
import com.cvberry.berrypim.Bootstrap;
import com.cvberry.berrypim.GitManager;
import com.cvberry.util.PasswordHasherLite;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
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
            IllegalAccessException, InstantiationException, ClassNotFoundException, URISyntaxException {
        Bootstrap.bootstrap("/berryPIM","../berryData");
    }

    static String pw1 =  "!thebuilder123!";
    static String wpw1 =  "hoax22";

    @Test
    public void testPasswordHasherLite() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        PasswordHasherLite liteHasher = new PasswordHasherLite();

        byte[] pw1Bytes =  pw1.getBytes("UTF-8");
        byte[] wpw1Bytes =  wpw1.getBytes("UTF-8");
        String hashedStr = liteHasher.createUsernameSaltHashStr("bob", pw1Bytes);

        PasswordHasherLite liteHasher2 = new PasswordHasherLite();
        PasswordHasherLite.AuthInfoHolder authInfo = PasswordHasherLite.retrieveSaltAndHash(hashedStr);
        assertTrue("password hasher properly recognizes correct password",liteHasher2.validatePassword(pw1Bytes,authInfo.salt,authInfo.hash));
        assertFalse("password hasher properly recognizes incorrect password",liteHasher2.validatePassword(wpw1Bytes,authInfo.salt,authInfo.hash));
    }

    @Test
    public void testAuthManager() throws IOException, NoSuchAlgorithmException {
        AuthenticationManager myManager = new AuthenticationManager("berrypim_test_passwords.txt");
        assertTrue(myManager.authenticateUser("vancan1ty","coolpassword"));
    }

    @Test
    public void testGitStatus() throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();
        GitManager.gitStatus("../berryData",output);
        System.out.println(output.toString());
        assertNotSame("can't be empty",output.toString(),"");
    }


}

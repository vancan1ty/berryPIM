package com.cvberry;

import com.cvberry.berrypim.Bootstrap;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
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
}

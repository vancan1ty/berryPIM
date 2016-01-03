package com.cvberry.berrypim;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by vancan1ty on 12/3/2015.
 *
 * Derived from "Validate" example on page 282 of Java in a Nutshelll, 5th Edition.
 */
public class XMLValidator {

    public static boolean validateDocument(File documentFile, File schemaFile, StringBuilder messageBuilder) throws IOException, ParserConfigurationException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = null;
        try { schema = factory.newSchema(schemaFile); }
        catch (SAXException e) {
            putFailureMessage(e,messageBuilder);
            return false;
        }

        Validator validator = schema.newValidator();

        SAXSource source = new SAXSource(new InputSource(new FileReader(documentFile)));

        //now validate the document
        try { validator.validate(source); }
        catch (SAXException e) {
            putFailureMessage(e,messageBuilder);
            return false;
        }

        return true;
    }

    static void putFailureMessage(SAXException e, StringBuilder messageBuilder) {
        if (e instanceof SAXParseException) {
            SAXParseException spe = (SAXParseException) e;
            messageBuilder.append(String.format("%s:%d:%d: %s%n",
                    spe.getSystemId(),spe.getLineNumber(),
                    spe.getColumnNumber(),spe.getMessage()));
        } else {
            messageBuilder.append(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        File documentFile = new File(args[0]);
        File schemaFile = new File(args[1]);

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = null;
        try { schema = factory.newSchema(schemaFile); }
        catch (SAXException e) {
            fail(e);
        }

        Validator validator = schema.newValidator();

        SAXSource source = new SAXSource(new InputSource(new FileReader(documentFile)));

        //now validate the document
        try { validator.validate(source); }
        catch (SAXException e) {fail(e);}

        System.out.println("Document is valid");
    }

    static void fail(SAXException e) {
        if (e instanceof SAXParseException) {
            SAXParseException spe = (SAXParseException) e;
            System.err.printf("%s:%d:%d: %s%n",
                    spe.getSystemId(),spe.getLineNumber(),
                    spe.getColumnNumber(),spe.getMessage());
        } else {
            System.err.println(e.getMessage());
        }
        System.exit(1);
    }
}

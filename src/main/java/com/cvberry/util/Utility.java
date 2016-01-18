package com.cvberry.util;

import com.cvberry.berrypim.Anchor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

/**
 * Created by vancan1ty on 1/2/2016.
 */
public class Utility {

    private static Object lock = new Object();

    //http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
    public static String slurp(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public static void spit(String path, String newContents) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(path)) {
            out.print(newContents);
        }
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static <L, R> List<Map.Entry<L, R>> tupleizeArray(Object[] arr) {
        List<Map.Entry<L, R>> out = new ArrayList<>();
        for (int i = 0; i < arr.length - 1; i += 2) {
            out.add(new AbstractMap.SimpleImmutableEntry<L, R>((L) arr[i], (R) arr[i + 1]));
        }
        return out;
    }

    public static String getPathComponentOrDefault(String[] pathComponents, int index, String defaultStr) {
        String selectorPath = (pathComponents.length < index + 1 || pathComponents[index] == null || pathComponents[index].isEmpty()) ?
                defaultStr : pathComponents[index];
        return selectorPath;
    }

    public static DocumentBuilderFactory getConfiguredDocBuilderFactory() {
        //Create a factory object for creating DOM parsers and configure it.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true); //We want to ignore comments
        factory.setCoalescing(true); // Convert CDATA to Text nodes
        factory.setNamespaceAware(false); // No namespaces: this is default
        factory.setValidating(false); // Don't validate DTD: also default

        return factory;
    }

    public static void displayImage(BufferedImage img) throws IOException, InterruptedException {
        ImageIcon icon = new ImageIcon(img);
        final JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(200, 300);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        Thread t = new Thread() {
            public void run() {
                synchronized (lock) {
                    while (frame.isVisible())
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    System.out.println("Working now");
                }
            }
        };
        t.start();

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent arg0) {
                synchronized (lock) {
                    System.out.println("closing window");
                    frame.setVisible(false);
                    lock.notify();
                }
            }

        });
        System.out.println("before join");
        t.join();
        System.out.println("after join");

    }

    public static String runXPathOnString(String documentStr, String query) throws IOException, SAXException,
            ParserConfigurationException, XPathExpressionException, TransformerException, XPathFactoryConfigurationException {
        DocumentBuilderFactory factory = Utility.getConfiguredDocBuilderFactory();

        //Now use the factory to create a DOM parser, a.k.a. DocumentBuilder
        DocumentBuilder parser = null;
        parser = factory.newDocumentBuilder();

        String xpathStr = query;
        Document document = null;
        document = parser.parse(new InputSource(new StringReader(documentStr)));

        XPath xPath = XPathFactory.newInstance().newXPath();
        //XPath xPath = Anchor.getInstance().getXPF().newXPath();
        Object oRes = xPath.compile(xpathStr).evaluate(document, XPathConstants.NODESET);
        StringBuilder out = new StringBuilder();
        NodeList result = (NodeList) oRes;
        for (int i = 0; i < result.getLength(); i++) {
            out.append(nodeToString(result.item(i)));
        }

        return out.toString();
    }

    static Transformer transformer = null;

    //http://stackoverflow.com/questions/4412848/xml-node-to-string-in-java
    private static String nodeToString(Node node) throws TransformerException {
        StringWriter sw = new StringWriter();
        removeEmptyText(node);
        if (transformer == null) {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        }

        transformer.transform(new DOMSource(node), new StreamResult(sw));
        return sw.toString();
    }


    public static void removeEmptyText(Node node) {
        Node child = node.getFirstChild();
        while (child != null) {
            Node sibling = child.getNextSibling();
            if (child.getNodeType() == Node.TEXT_NODE) {
                if (child.getTextContent().trim().isEmpty())
                    node.removeChild(child);
            } else
                removeEmptyText(child);
            child = sibling;
        }
    }

    static public String join(String joiner, Collection<String> strings) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : strings) {
            if (first) {
                first = false;
            } else {
                sb.append(joiner);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * [CB 1/6/16] REMEMBER: revisit this if berryPIM is ever the basis for a public facing app -- may need
     * more complex rules to prevent XSS.
     * http://stackoverflow.com/questions/1265282/recommended-method-for-escaping-html-in-java
     *
     * @param s
     * @return
     */
    public static String escapeXML(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    //http://stackoverflow.com/questions/13592236/parse-the-uri-string-into-name-value-collection-in-java
    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static String getFirstQParamResult(Map<String, String[]> queryParams, String key) {
        if (queryParams.containsKey(key) && queryParams.get(key).length > 0) {
            return queryParams.get(key)[0];
        } else {
            return null;
        }
    }

     public static String realDecode(String toDecode) throws UnsupportedEncodingException {
         String s1 = toDecode.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
         String s2 = s1.replaceAll("\\+", "%2B");
         String s3 = URLDecoder.decode(s2, "utf-8");
         return s3;
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

    public static String collectExceptionToString(Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

}

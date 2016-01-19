package com.cvberry.util;

import net.sf.saxon.s9api.*;
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
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
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

    public static String runXQueryOnString(String documentStr, String query) throws IOException, SAXException,
            ParserConfigurationException, XPathExpressionException, TransformerException, XPathFactoryConfigurationException, SaxonApiException {

        Processor sxProcessor = new Processor(false);
        net.sf.saxon.s9api.DocumentBuilder myBuilder = sxProcessor.newDocumentBuilder();
        //myBuilder.setLineNumbering(true);
        //myBuilder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL); this line doesn't work.
        InputStream docStream = new ByteArrayInputStream(documentStr.getBytes("UTF-8"));
        Source source = new StreamSource(docStream);
        XdmNode parsedDoc = myBuilder.build(source);
        XQueryCompiler compiler = sxProcessor.newXQueryCompiler();
        XQueryExecutable compQuery = compiler.compile(query);


        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Serializer outSerializer = sxProcessor.newSerializer(outStream);
        outSerializer.setOutputProperty(Serializer.Property.METHOD, "xml");
        outSerializer.setOutputProperty(Serializer.Property.INDENT, "yes");
        outSerializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");

        XQueryEvaluator evaluator = compQuery.load();
        evaluator.setContextItem(parsedDoc);
        evaluator.run(outSerializer);

//        StringBuilder out = new StringBuilder();
//
//        for (Object item : evaluator) {
//            if (XdmNode.class.isAssignableFrom(item.getClass())) {
//                XdmNode node = (XdmNode) item;
//                int lineNumber = node.getLineNumber();
//                out.append(lineNumber + "\n");
//                out.append(getFullXPath(node) + "\n");
//                out.append(node.toString() +"\n");
//            }
//        }
//        return out.toString();
        String out = outStream.toString("UTF-8");
        return out;
    }

    public static XdmSequenceIterator runXQueryOnStringToDS(String documentStr, String query) throws IOException, SAXException,
            ParserConfigurationException, XPathExpressionException, TransformerException, XPathFactoryConfigurationException, SaxonApiException {

        Processor sxProcessor = new Processor(false);
        net.sf.saxon.s9api.DocumentBuilder myBuilder = sxProcessor.newDocumentBuilder();
        //myBuilder.setLineNumbering(true);
        //myBuilder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL); this line doesn't work.
        InputStream docStream = new ByteArrayInputStream(documentStr.getBytes("UTF-8"));
        Source source = new StreamSource(docStream);
        XdmNode parsedDoc = myBuilder.build(source);
        XQueryCompiler compiler = sxProcessor.newXQueryCompiler();
        XQueryExecutable compQuery = compiler.compile(query);


        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Serializer outSerializer = sxProcessor.newSerializer(outStream);
        outSerializer.setOutputProperty(Serializer.Property.METHOD, "xml");
        outSerializer.setOutputProperty(Serializer.Property.INDENT, "yes");
        outSerializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");

        XQueryEvaluator evaluator = compQuery.load();
        evaluator.setContextItem(parsedDoc);
        evaluator.run(outSerializer);

//        StringBuilder out = new StringBuilder();
//
//        for (Object item : evaluator) {
//            if (XdmNode.class.isAssignableFrom(item.getClass())) {
//                XdmNode node = (XdmNode) item;
//                int lineNumber = node.getLineNumber();
//                out.append(lineNumber + "\n");
//                out.append(getFullXPath(node) + "\n");
//                out.append(node.toString() +"\n");
//            }
//        }
//        return out.toString();
        return evaluator.iterator();
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
        if (toWriteTo == null) {
            nNullStrBuilder = new StringBuilder();
        }

        ProcessBuilder pb = new ProcessBuilder(segments);
        pb.directory(homeDir);
        Process p = pb.start();
        if (toPassToInput != null) {
            OutputStream outS = p.getOutputStream();
            outS.write(toPassToInput.getBytes("UTF-8"));
            outS.flush();
            outS.close();
        }

        p.waitFor();
        int out = p.exitValue();
        String stdOut = Utility.convertStreamToString(p.getInputStream());
        String stdErr = Utility.convertStreamToString(p.getErrorStream());
        nNullStrBuilder.append("output\n" + stdOut + "\n");
        nNullStrBuilder.append("error\n" + stdErr + "\n");
        return out;
    }

    public static String collectExceptionToString(Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public static String getFullXPath(XdmNode n) {
// abort early
        if (null == n)
            return null;

// declarations
        XdmNode parent = null;
        Stack<XdmNode> hierarchy = new Stack<>();
        StringBuffer buffer = new StringBuffer();

// push element on stack
        hierarchy.push(n);

        switch (n.getNodeKind()) {
            case ATTRIBUTE:
                parent = n.getParent();
                break;
            case ELEMENT:
                parent = n.getParent();
                break;
            case DOCUMENT:
                parent = n.getParent();
                break;
            default:
                throw new IllegalStateException("Unexpected Node type" + n.getNodeKind());
        }

        while (null != parent && parent.getNodeKind() != XdmNodeKind.DOCUMENT) {
            // push on stack
            hierarchy.push(parent);

            // get parent of parent
            parent = parent.getParent();
        }

        // construct xpath
        Object obj = null;
        while (!hierarchy.isEmpty() && null != (obj = hierarchy.pop())) {
            XdmNode node = (XdmNode) obj;
            boolean handled = false;

            if (node.getNodeKind() == XdmNodeKind.ELEMENT) {
                // is this the root element?
                if (buffer.length() == 0) {
                    // root element - simply append element name
                    buffer.append(node.getNodeName());
                } else {
                    // child element - append slash and element name
                    buffer.append("/");
                    buffer.append(node.getNodeName());

                    if (true) {
                        // see if the element has a name or id attribute
                        if (node.getAttributeValue(new QName("id")) != null) {
                            // id attribute found - use that
                            buffer.append("[@id='" + node.getAttributeValue(new QName("id")) + "']");
                            handled = true;
                        } else if (node.getAttributeValue(new QName("name")) != null) {
                            // name attribute found - use that
                            buffer.append("[@name='" + node.getAttributeValue(new QName("name")) + "']");
                            handled = true;
                        }
                    }

                    if (!handled) {
                        // no known attribute we could use - get sibling index
                        int prev_siblings = 1;

                        XdmSequenceIterator prevIterator = node.axisIterator(Axis.PRECEDING_SIBLING);
                        while (prevIterator.hasNext()) {
                            XdmItem next = prevIterator.next();
                            if (XdmNode.class.isAssignableFrom(next.getClass())) {
                                XdmNode rNext = (XdmNode) next;
                                if (rNext.getNodeKind().equals(node.getNodeKind())) {
                                    if (rNext.getNodeName().getLocalName().equalsIgnoreCase(node.getNodeName().getLocalName())) {
                                        prev_siblings++;
                                    }
                                }
                            }
                        }
                        buffer.append("[" + prev_siblings + "]");
//                        while (null != prev_sibling) {
//                            if (prev_sibling.getNodeType() == node.getNodeType()) {
//                                if (prev_sibling.getNodeName().equalsIgnoreCase(
//                                        node.getNodeName())) {
//                                    prev_siblings++;
//                                }
//                            }
//                            prev_sibling = prev_sibling.getPreviousSibling();
//                      }
                    }
                }
            } else if (node.getNodeKind() == XdmNodeKind.ATTRIBUTE) {
                buffer.append("/@");
                buffer.append(node.getNodeName());
            }
        }
// return buffer
        return buffer.toString();
    }

    public static boolean nullOrEmpty(String s) {
        if (s==null || s.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }
}

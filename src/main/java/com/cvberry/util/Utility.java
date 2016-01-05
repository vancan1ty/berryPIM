package com.cvberry.util;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        JFrame frame = new JFrame();
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
}

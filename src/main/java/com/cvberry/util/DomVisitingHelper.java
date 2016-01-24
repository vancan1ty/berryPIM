package com.cvberry.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by vancan1ty on 1/22/2016.
 */
public class DomVisitingHelper {

//    public static void domVisitTree(Document doc, Consumer<Node> funcToDo) {
//        domVisitTree(doc.getParentNode(), funcToDo);
//    }
//
//    public static void domVisitTree(Node node, Function<Node,Boolean> funcToDo) {
//        boolean recurse = funcToDo.apply(node);
//        NodeList children = node.getChildNodes();
//        for (int i = 0; i < children.getLength(); i++) {
//           Node child = children.item(i);
//           funcToDo.accept()
//        }
//    }
}

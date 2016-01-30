package com.cvberry.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by vancan1ty on 1/22/2016.
 */
public class DomVisitingHelper {

    public static int domVisitTree(Document doc, ItemVisitorHelper funcToDo) {
        Node firstChild = doc.getFirstChild();
        if(firstChild == null) {
            System.out.println("empty document!");
            return -1;
        }
        return domVisitTree(firstChild,funcToDo);
    }

    /**
     * returns the count of the number of nodes for which 'funcToDo' returned false.
     * @param node
     * @param funcToDo
     * @return
     */
    public static int domVisitTree(Node node, ItemVisitorHelper funcToDo) {
        int count = 0;
        boolean recurse = funcToDo.apply(node);
        if (!recurse) {
            count++;
        }
        if(recurse) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                count += domVisitTree(child,funcToDo);
            }
        }
        return count;
    }


    public static abstract class ItemVisitorHelper {

        /**
         * @param node
         * @return true IFF the handler should recurse to this node's children.
         */
        public abstract Boolean apply(Node node);


        /**
         * this function is intended to be called after the traversal of the tree has been accomplished
         */
        public abstract void doFinalAction();
    }

}

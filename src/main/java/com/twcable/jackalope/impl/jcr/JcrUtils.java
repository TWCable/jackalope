package com.twcable.jackalope.impl.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Iterator;

/**
 * Utility class for JCR functions.
 */
public final class JcrUtils {

    private JcrUtils() {
    }


    public static Iterable<Node> getChildNodes(final Node node) {
        return new Iterable<Node>() {
            @SuppressWarnings("unchecked")
            @Override
            public Iterator<Node> iterator() {
                try {
                    return node.getNodes();
                }
                catch (RepositoryException re) {
                    return new Iterator<Node>() {
                        @Override
                        public boolean hasNext() {
                            return false;
                        }


                        @Override
                        public Node next() {
                            return null;
                        }


                        @Override
                        public void remove() {
                        }
                    };
                }
            }
        };
    }
}

package com.twcable.jackalope.impl.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.Collection;

class NodeIteratorImpl extends RangeIteratorImpl<Node> implements NodeIterator {
    NodeIteratorImpl(Collection<Node> nodes) {
        super(nodes);
    }


    @Override
    public Node nextNode() {
        return (Node)next();
    }
}

package com.twcable.jackalope.impl.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import java.util.Arrays;

/**
 * Simple implementation of an {@link QueryResult}
 */
public class QueryResultImpl implements QueryResult {
    private Node[] nodes = new Node[0];


    public QueryResultImpl(Node... nodes) {
        this.nodes = nodes;
    }


    @Override
    public String[] getColumnNames() throws RepositoryException {
        return new String[0];
    }


    @Override
    public RowIterator getRows() throws RepositoryException {
        return null;
    }


    @Override
    public NodeIterator getNodes() throws RepositoryException {
        return new NodeIteratorImpl(Arrays.asList(nodes));
    }


    @Override
    public String[] getSelectorNames() throws RepositoryException {
        return new String[0];
    }
}

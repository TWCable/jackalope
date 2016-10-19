/*
 * Copyright 2014-2016 Time Warner Cable, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

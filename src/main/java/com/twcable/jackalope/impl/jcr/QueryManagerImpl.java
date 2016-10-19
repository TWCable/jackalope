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
import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.qom.QueryObjectModelFactory;

/**
 * Simple implementation of an {@link QueryManager}
 */
@SuppressWarnings("DuplicateThrows")
public class QueryManagerImpl implements QueryManager {
    private Query[] queries = new QueryImpl[0];


    public QueryManagerImpl(Query... queries) {
        this.queries = queries;
    }


    @Override
    public Query createQuery(String statement, String language) throws InvalidQueryException, RepositoryException {
        for (Query query : queries)
            if (query.getLanguage().equals(language) && query.getStatement().equals(statement))
                return query;
        return new QueryImpl(statement, language, new QueryResultImpl());
    }


    @Override
    public QueryObjectModelFactory getQOMFactory() {
        return null;
    }


    @Override
    public Query getQuery(Node node) throws InvalidQueryException, RepositoryException {
        return null;
    }


    @Override
    public String[] getSupportedQueryLanguages() throws RepositoryException {
        return new String[0];
    }
}

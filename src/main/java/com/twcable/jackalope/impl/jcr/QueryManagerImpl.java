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

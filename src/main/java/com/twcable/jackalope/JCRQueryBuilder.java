/*
 * Copyright 2015 Time Warner Cable, Inc.
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

package com.twcable.jackalope;

import com.twcable.jackalope.impl.jcr.QueryImpl;
import com.twcable.jackalope.impl.jcr.QueryManagerImpl;
import com.twcable.jackalope.impl.jcr.QueryResultImpl;
import com.twcable.jackalope.impl.jcr.WorkspaceImpl;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.List;

public class JCRQueryBuilder {

    @Nonnull
    public static QueryManagerBuilder queryManager(Session session, QueryBuilder... queryBuilders) {
        return new QueryManagerBuilder(session, queryBuilders);
    }


    @Nonnull
    public static QueryBuilder query(String statement, String language, QueryResultBuilder queryResultBuilder) {
        return new QueryBuilder(statement, language, queryResultBuilder);
    }


    @Nonnull
    public static QueryResultBuilder result(Node... nodes) {
        return new QueryResultBuilder(nodes);
    }

    // **********************************************************************
    //
    // INNER CLASSES
    //
    // **********************************************************************


    static class QueryManagerBuilder {
        private Session session;
        private QueryBuilder[] queryBuilders;


        public QueryManagerBuilder(Session session, QueryBuilder... queryBuilders) {
            this.session = session;
            this.queryBuilders = queryBuilders;
        }


        public QueryManager build() {
            List<Query> queries = new ArrayList<>();
            for (QueryBuilder queryBuilder : queryBuilders)
                queries.add(queryBuilder.build());
            QueryManager queryManager = new QueryManagerImpl(queries.toArray(new Query[queries.size()]));
            ((WorkspaceImpl)session.getWorkspace()).setQueryManager(queryManager);
            return queryManager;
        }
    }

    static class QueryBuilder {
        String statement;
        String language;
        QueryResultBuilder queryResultBuilder;


        public QueryBuilder(String statement, String language, QueryResultBuilder queryResultBuilder) {
            this.statement = statement;
            this.language = language;
            this.queryResultBuilder = queryResultBuilder;
        }


        public Query build() {
            return new QueryImpl(statement, language, queryResultBuilder.build());
        }
    }

    static class QueryResultBuilder {
        Node[] nodes = new Node[0];


        public QueryResultBuilder(Node... nodes) {
            this.nodes = nodes;
        }


        public QueryResult build() {
            return new QueryResultImpl(nodes);
        }
    }

}

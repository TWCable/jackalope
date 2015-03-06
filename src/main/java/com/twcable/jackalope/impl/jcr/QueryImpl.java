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

package com.twcable.jackalope.impl.jcr;

import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionException;

/**
 * Simple implementation of an {@link Query}
 */
@SuppressWarnings("DuplicateThrows")
public class QueryImpl implements Query {
    String statement = null;
    String language = null;
    QueryResult result = null;


    public QueryImpl(String statement, String language, QueryResult result) {
        this.statement = statement;
        this.language = language;
        this.result = result;
    }


    @Override
    public QueryResult execute() throws InvalidQueryException, RepositoryException {
        return result;
    }


    @Override
    public void setLimit(long limit) {
    }


    @Override
    public void setOffset(long offset) {
    }


    @Override
    public String getStatement() {
        return statement;
    }


    @Override
    public String getLanguage() {
        return language;
    }


    @Override
    public String getStoredQueryPath() throws ItemNotFoundException, RepositoryException {
        return null;
    }


    @Override
    public Node storeAsNode(String absPath) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, UnsupportedRepositoryOperationException, RepositoryException {
        return null;
    }


    @Override
    public void bindValue(String varName, Value value) throws IllegalArgumentException, RepositoryException {
    }


    @Override
    public String[] getBindVariableNames() throws RepositoryException {
        return new String[0];
    }
}

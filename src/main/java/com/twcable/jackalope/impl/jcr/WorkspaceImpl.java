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

import org.xml.sax.ContentHandler;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.ItemExistsException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.lock.LockManager;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.observation.ObservationManager;
import javax.jcr.query.QueryManager;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionManager;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple implementation of {@link Workspace}
 */
@SuppressWarnings("DuplicateThrows")
public class WorkspaceImpl implements Workspace {
    private Session session = null;
    private ObservationManager observationManager = null;

    protected QueryManager queryManager = null;


    @Override
    public Session getSession() {
        return session;
    }


    @Override
    public String getName() {
        return null;
    }


    @Override
    public void copy(String srcAbsPath, String destAbsPath) throws ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException {
    }


    @Override
    public void copy(String srcWorkspace, String srcAbsPath, String destAbsPath) throws NoSuchWorkspaceException, ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException {
    }


    @Override
    public void clone(String srcWorkspace, String srcAbsPath, String destAbsPath, boolean removeExisting) throws NoSuchWorkspaceException, ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException {
    }


    @Override
    public void move(String srcAbsPath, String destAbsPath) throws ConstraintViolationException, VersionException, AccessDeniedException, PathNotFoundException, ItemExistsException, LockException, RepositoryException {
    }


    @Override
    public void restore(Version[] versions, boolean removeExisting) throws ItemExistsException, UnsupportedRepositoryOperationException, VersionException, LockException, InvalidItemStateException, RepositoryException {
    }


    @Override
    public LockManager getLockManager() throws UnsupportedRepositoryOperationException, RepositoryException {
        return null;
    }


    @Override
    public QueryManager getQueryManager() throws RepositoryException {
        return queryManager;
    }


    @Override
    public NamespaceRegistry getNamespaceRegistry() throws RepositoryException {
        return null;
    }


    @Override
    public NodeTypeManager getNodeTypeManager() throws RepositoryException {
        return null;
    }


    @Override
    public ObservationManager getObservationManager() throws UnsupportedRepositoryOperationException, RepositoryException {
        if (observationManager == null) {
            observationManager = new ObservationManagerImpl();
        }
        return observationManager;
    }


    @Override
    public VersionManager getVersionManager() throws UnsupportedRepositoryOperationException, RepositoryException {
        return null;
    }


    @Override
    public String[] getAccessibleWorkspaceNames() throws RepositoryException {
        return new String[0];
    }


    @Override
    public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior) throws PathNotFoundException, ConstraintViolationException, VersionException, LockException, AccessDeniedException, RepositoryException {
        return null;
    }


    @Override
    public void importXML(String parentAbsPath, InputStream in, int uuidBehavior) throws IOException, VersionException, PathNotFoundException, ItemExistsException, ConstraintViolationException, InvalidSerializedDataException, LockException, AccessDeniedException, RepositoryException {
    }


    @Override
    public void createWorkspace(String name) throws AccessDeniedException, UnsupportedRepositoryOperationException, RepositoryException {
    }


    @Override
    public void createWorkspace(String name, String srcWorkspace) throws AccessDeniedException, UnsupportedRepositoryOperationException, NoSuchWorkspaceException, RepositoryException {
    }


    @Override
    public void deleteWorkspace(String name) throws AccessDeniedException, UnsupportedRepositoryOperationException, NoSuchWorkspaceException, RepositoryException {
    }


    public void setSession(Session session) {
        this.session = session;
    }


    public void setQueryManager(QueryManager queryManager) {
        this.queryManager = queryManager;
    }
}

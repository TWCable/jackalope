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

import org.apache.sling.jcr.api.SlingRepository;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

/**
 * Simple implementation of an {@link SlingRepository}
 */
@SuppressWarnings("DuplicateThrows")
public class RepositoryImpl implements SlingRepository {
    private final SessionImpl session;


    public RepositoryImpl() {
        this.session = new SessionImpl(this);
    }


    @Override
    public String[] getDescriptorKeys() {
        return new String[0];
    }


    @Override
    public boolean isStandardDescriptor(String key) {
        return false;
    }


    @Override
    public boolean isSingleValueDescriptor(String key) {
        return false;
    }


    @Override
    public Value getDescriptorValue(String key) {
        return null;
    }


    @Override
    public Value[] getDescriptorValues(String key) {
        return new Value[0];
    }


    @Override
    public String getDescriptor(String key) {
        return null;
    }


    @Override
    public Session login(Credentials credentials, String workspaceName) throws LoginException, NoSuchWorkspaceException, RepositoryException {
        return session;
    }


    @Override
    public Session login(Credentials credentials) throws LoginException, RepositoryException {
        return session;
    }


    @Override
    public Session login(String workspaceName) throws LoginException, NoSuchWorkspaceException, RepositoryException {
        return session;
    }


    @Override
    public Session login() throws LoginException, RepositoryException {
        return session;
    }


    @Override
    public String getDefaultWorkspace() {
        return null; // Unimplemented
    }


    @Override
    public Session loginAdministrative(String workspace) throws RepositoryException {
        return session;
    }


    @Override
    public Session loginService(String s, String s1) throws LoginException, RepositoryException {
        return session;
    }

    @Override
    public Session impersonateFromService(String s, Credentials credentials, String s1) throws LoginException, RepositoryException {
        throw new UnsupportedOperationException();
    }

}

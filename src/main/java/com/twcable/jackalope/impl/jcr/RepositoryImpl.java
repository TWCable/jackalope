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
}

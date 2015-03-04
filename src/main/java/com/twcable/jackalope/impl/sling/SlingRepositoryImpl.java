package com.twcable.jackalope.impl.sling;

import com.twcable.jackalope.impl.jcr.RepositoryImpl;
import org.apache.sling.jcr.api.SlingRepository;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class SlingRepositoryImpl extends RepositoryImpl implements SlingRepository {

    @Override
    public String getDefaultWorkspace() {
        return null;
    }


    @Override
    public Session loginAdministrative(String workspace) throws RepositoryException {
        return login();
    }

}

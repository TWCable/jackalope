package com.twcable.jackalope.impl.jcr;

import javax.jcr.RepositoryException;
import javax.jcr.observation.EventJournal;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.EventListenerIterator;
import javax.jcr.observation.ObservationManager;

/**
 * Simple implementation of an {@link ObservationManager}
 */
public class ObservationManagerImpl implements ObservationManager {

    @Override
    public void addEventListener(EventListener listener, int eventTypes, String absPath, boolean isDeep, String[] uuid, String[] nodeTypeName, boolean noLocal) throws RepositoryException {
    }


    @Override
    public void removeEventListener(EventListener listener) throws RepositoryException {
    }


    @Override
    public EventListenerIterator getRegisteredEventListeners() throws RepositoryException {
        return null;
    }


    @Override
    public void setUserData(String userData) throws RepositoryException {
    }


    @Override
    public EventJournal getEventJournal() throws RepositoryException {
        return null;
    }


    @Override
    public EventJournal getEventJournal(int eventTypes, String absPath, boolean isDeep, String[] uuid, String[] nodeTypeName) throws RepositoryException {
        return null;
    }
}

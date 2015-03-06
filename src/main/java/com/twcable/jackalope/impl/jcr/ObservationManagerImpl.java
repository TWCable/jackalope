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

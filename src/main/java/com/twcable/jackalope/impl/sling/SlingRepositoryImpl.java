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

package com.twcable.jackalope.impl.sling;

import com.twcable.jackalope.impl.jcr.RepositoryImpl;
import org.apache.sling.jcr.api.SlingRepository;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
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

    @Override
    public Session impersonateFromService(String s, Credentials credentials, String s1) throws LoginException, RepositoryException {
        throw new UnsupportedOperationException();
    }

}

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
package com.twcable.jackalope.impl.sling;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A trivial {@link ResourceResolverFactory} that creates a {@link ResourceResolverImpl} based on a
 * {@link SlingRepository} and always returns the same instance.
 */
public class SimpleResourceResolverFactory implements ResourceResolverFactory {
    private ResourceResolver resourceResolver = null;


    public SimpleResourceResolverFactory(@Nonnull SlingRepository repository) {
        this.resourceResolver = new ResourceResolverImpl(repository);
    }


    @Override
    public ResourceResolver getResourceResolver(Map<String, Object> authenticationInfo) throws LoginException {
        return this.resourceResolver;
    }


    @Override
    public ResourceResolver getAdministrativeResourceResolver(Map<String, Object> authenticationInfo) throws LoginException {
        return this.resourceResolver;
    }


    @Override
    public ResourceResolver getServiceResourceResolver(Map<String, Object> authenticationInfo) throws LoginException {
        return this.resourceResolver;
    }

}

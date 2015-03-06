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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Iterator;

import static org.apache.sling.jcr.resource.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY;

/**
 * Basic abstract class to implement an {@link Item} as a {@link Resource}
 */
abstract public class ItemResourceImpl implements Resource {
    final ResourceResolver resourceResolver;
    final Item item;


    public ItemResourceImpl(ResourceResolver resourceResolver, Item item) {
        this.resourceResolver = resourceResolver;
        this.item = item;
    }


    @Override
    public String getPath() {
        try {
            return item.getPath();
        }
        catch (RepositoryException re) {
            throw new SlingRepositoryException(re);
        }
    }


    @Override
    public String getName() {
        try {
            return item.getName();
        }
        catch (RepositoryException re) {
            throw new SlingRepositoryException(re);
        }
    }


    @Override
    public Iterator<Resource> listChildren() {
        return null;
    }


    @Override
    public Resource getParent() {
        try {
            return resourceResolver.getResource(item.getParent().getPath());
        }
        catch (RepositoryException re) {
            throw new SlingRepositoryException(re);
        }
    }


    @Override
    public Resource getChild(String relPath) {
        return null;
    }


    @Override
    public String getResourceType() {
        return null;
    }


    @Override
    public String getResourceSuperType() {
        return null;
    }


    @Override
    public boolean isResourceType(String resourceType) {
        return resourceType.equals(getResourceType());
    }


    @Override
    public ResourceMetadata getResourceMetadata() {
        ResourceMetadata metadata = new ResourceMetadata();
        metadata.setResolutionPath(getPath());
        return metadata;
    }


    @Override
    public ResourceResolver getResourceResolver() {
        return resourceResolver;
    }


    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        return null;
    }


    protected String getResourceTypeForNode(Node node) throws RepositoryException {
        return node.hasProperty(SLING_RESOURCE_TYPE_PROPERTY)
            ? node.getProperty(SLING_RESOURCE_TYPE_PROPERTY).getString()
            : node.getPrimaryNodeType().getName();
    }

}

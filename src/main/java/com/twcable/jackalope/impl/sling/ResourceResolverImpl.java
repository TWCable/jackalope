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

import com.day.cq.wcm.api.PageManager;
import com.twcable.jackalope.impl.common.Paths;
import com.twcable.jackalope.impl.cq.PageManagerImpl;
import com.twcable.jackalope.impl.jcr.NodeImpl;
import com.twcable.jackalope.impl.jcr.SessionImpl;
import com.twcable.jackalope.impl.jcr.ValueImpl;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link ResourceResolver} suitable for testing.
 */
public class ResourceResolverImpl implements ResourceResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceResolverImpl.class);

    private final Session session;
    private boolean isLive = true;


    @SuppressWarnings("ConstantConditions")
    public ResourceResolverImpl(@Nonnull SlingRepository repository) {
        if (repository == null) throw new IllegalArgumentException("Repository cannot be null.");
        try {
            this.session = repository.login();
        }
        catch (RepositoryException re) {
            throw new SlingRepositoryException(re);
        }
    }


    @Override
    public Resource resolve(HttpServletRequest request, String absPath) {
        String path = absPath != null ? absPath : "/";
        Resource resource = getResource(path);
        return resource != null ? resource : new NonExistingResource(this, path);
    }


    @Override
    public Resource resolve(String absPath) {
        return resolve(null, absPath);
    }


    @Override
    public Resource resolve(HttpServletRequest request) {
        return resolve(request, null);
    }


    @Override
    public String map(String resourcePath) {
        LOG.warn("Resource mapping is not implemented");
        return resourcePath;  //TODO: implement resource mapping
    }


    @Override
    public String map(HttpServletRequest request, String resourcePath) {
        LOG.warn("Resource mapping is not implemented");
        return resourcePath;  //TODO: implement resource mapping
    }


    @Override
    public Resource getResource(String path) {
        try {
            return (session.itemExists(path)) ? constructResource(session.getItem(path)) : null;
        }
        catch (RepositoryException re) {
            throw new SlingRepositoryException(re);
        }
    }


    @Override
    public Resource getResource(Resource base, String path) {
        return getResource(Paths.resolve(base.getPath(), path));
    }


    @Override
    public String[] getSearchPath() {
        return new String[0];  //TODO
    }


    @Override
    public Iterator<Resource> listChildren(Resource parent) {
        return parent.listChildren();
    }


    @Override
    public Iterable<Resource> getChildren(Resource resource) {
        return resource.getChildren();
    }


    @Override
    public Iterator<Resource> findResources(String query, String language) {
        return null;  //TODO: Implement queries
    }


    @Override
    public Iterator<Map<String, Object>> queryResources(String query, String language) {
        return null;  //TODO: Implement queries
    }


    @Override
    public boolean hasChildren(Resource resource) {
        return listChildren(resource).hasNext();
    }


    @Override
    public ResourceResolver clone(Map<String, Object> authenticationInfo) throws LoginException {
        return null;  //TODO: Implement authentication
    }


    @Override
    public boolean isLive() {
        return isLive;
    }


    @Override
    public void close() {
        isLive = false;
    }


    @Override
    public String getUserID() {
        return null;  //TODO: Implement authentication
    }


    @Override
    public Iterator<String> getAttributeNames() {
        return null;  //TODO: Implement attributes
    }


    @Override
    public Object getAttribute(String name) {
        return null;  //TODO: Implement attributes
    }


    @Override
    public void delete(Resource resource) throws PersistenceException {
        try {
            session.removeItem(resource.getPath());
        }
        catch (RepositoryException e) {
            throw new PersistenceException("Could not delete " + resource.getPath(), e);
        }
    }


    @Override
    public Resource create(@Nonnull Resource parent, @Nonnull String name, @Nullable Map<String, Object> properties) throws PersistenceException {
        //noinspection ConstantConditions
        if (parent == null)
            throw new IllegalArgumentException("Could not create a node for \"" + name + "\" because the parent is null");
        String parentPath = parent.getPath();

        // remove any trailing slash (or sole-slash if the root)
        if (parentPath.endsWith("/")) parentPath = parentPath.substring(0, parentPath.length() - 1);
        String path = parentPath + "/" + name;

        return createNodeResource(path, properties != null ? properties : Collections.<String, Object>emptyMap());
    }


    private Resource createNodeResource(@Nonnull String path, @Nonnull Map<String, Object> properties) {
        try {
            if (session.nodeExists(path)) return new NodeResourceImpl(this, session.getNode(path));
        }
        catch (RepositoryException e) {
            //should be impossible since this is in-memory
            throw new IllegalStateException(e);
        }

        try {
            NodeImpl node = createNode(path, properties);
            return new NodeResourceImpl(this, node);
        }
        catch (ItemNotFoundException | ItemExistsException e) {
            // should be impossible since we're checking first
            throw new IllegalStateException(e);
        }
    }


    private NodeImpl createNode(@Nonnull String path, @Nonnull Map<String, Object> properties) throws ItemNotFoundException, ItemExistsException {
        NodeImpl node = new NodeImpl((SessionImpl)session, path);

        for (String propName : properties.keySet()) {
            Object mapVal = properties.get(propName);
            setNodeProperty(node, propName, mapVal);
        }
        return node;
    }


    private static void setNodeProperty(@Nonnull NodeImpl node, @Nonnull String propName, @Nonnull Object propertyVal) {
        try {
            node.setProperty(propName, new ValueImpl(propertyVal));
        }
        catch (RepositoryException ignore) {
            // ignore
        }
    }


    @Override
    public void revert() {
    }


    @Override
    public void commit() throws PersistenceException {
    }


    @Override
    public boolean hasChanges() {
        return false;
    }


    @Override
    public String getParentResourceType(Resource resource) {
        return resource.getParent().getResourceType();
    }


    @Override
    public String getParentResourceType(String s) {
        return null;
    }


    @Override
    public boolean isResourceType(Resource resource, String resourceType) {
        return resource.isResourceType(resourceType);
    }


    @Override
    public void refresh() {
    }


    @Override
    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        if (type.equals(Session.class)) return (AdapterType)session;
        if (type.equals(PageManager.class)) return (AdapterType)new PageManagerImpl(this);
        else return null;
    }


    private Resource constructResource(Item item) {
        return (item.isNode()) ? new NodeResourceImpl(this, (Node)item) : new PropertyResourceImpl(this, (Property)item);
    }

}

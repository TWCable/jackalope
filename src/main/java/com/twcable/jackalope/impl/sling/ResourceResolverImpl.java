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
import lombok.val;
import org.apache.jackrabbit.spi.commons.iterator.Iterators;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.api.SlingRepository;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public ResourceResolverImpl(SlingRepository repository) {
        if (repository == null) throw new IllegalArgumentException("Repository cannot be null.");
        try {
            this.session = repository.login();
        }
        catch (RepositoryException re) {
            throw new SlingRepositoryException(re);
        }
    }


    @Override
    public Resource resolve(@Nullable HttpServletRequest request, @Nullable String absPath) {
        String path = absPath != null ? absPath : "/";
        Resource resource = getResource(path);
        return resource != null ? resource : new NonExistingResource(this, path);
    }


    @Override
    public Resource resolve(@Nullable String absPath) {
        return resolve(null, absPath);
    }


    @Override
    public Resource resolve(@Nullable HttpServletRequest request) {
        return resolve(request, null);
    }


    @Override
    public @Nullable String map(@Nullable String resourcePath) {
        LOG.warn("Resource mapping is not implemented");
        return resourcePath;  //TODO: implement resource mapping
    }


    @Override
    public @Nullable String map(@Nullable HttpServletRequest request, @Nullable String resourcePath) {
        LOG.warn("Resource mapping is not implemented");
        return resourcePath;  //TODO: implement resource mapping
    }


    @Override
    public @Nullable Resource getResource(@Nullable String path) {
        try {
            return (session.itemExists(path)) ? constructResource(session.getItem(path)) : null;
        }
        catch (RepositoryException re) {
            throw new SlingRepositoryException(re);
        }
    }


    @Override
    public @Nullable Resource getResource(@Nullable Resource base, @Nullable String path) {
        if (base == null) return getResource(Paths.resolve("/", path));
        return getResource(Paths.resolve(base.getPath(), path));
    }


    @Override
    public String[] getSearchPath() {
        return new String[0];  //TODO
    }


    @Override
    public Iterator<Resource> listChildren(@Nullable Resource parent) {
        if (parent == null) return Iterators.empty();
        return parent.listChildren();
    }


    @Override
    public Iterable<Resource> getChildren(@Nullable Resource resource) {
        if (resource == null) return Collections.emptyList();
        return resource.getChildren();
    }


    @Override
    public Iterator<Resource> findResources(@Nullable String query, @Nullable String language) {
        return Iterators.empty(); //TODO: Implement queries
    }


    @Override
    public Iterator<Map<String, Object>> queryResources(@Nullable String query, @Nullable String language) {
        return Iterators.empty(); //TODO: Implement queries
    }


    @Override
    public boolean hasChildren(@Nullable Resource resource) {
        return listChildren(resource).hasNext();
    }


    @Override
    public ResourceResolver clone(@Nullable Map<String, Object> authenticationInfo) throws LoginException {
        throw new LoginException("Not implemented");  //TODO: Implement authentication
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
        return "";  //TODO: Implement authentication
    }


    @Override
    public Iterator<String> getAttributeNames() {
        return Iterators.empty();  //TODO: Implement attributes
    }


    @Override
    public @Nullable Object getAttribute(@Nullable String name) {
        return null;  //TODO: Implement attributes
    }


    @Override
    public void delete(@Nullable Resource resource) throws PersistenceException {
        if (resource == null) throw new PersistenceException("resource == null");
        try {
            session.removeItem(resource.getPath());
        }
        catch (RepositoryException e) {
            throw new PersistenceException("Could not delete " + resource.getPath(), e);
        }
    }


    @Override
    public Resource create(@Nullable Resource parent, @Nullable String name, @Nullable Map<String, Object> properties) throws PersistenceException {
        //noinspection ConstantConditions
        if (parent == null)
            throw new IllegalArgumentException("Could not create a node for \"" + name + "\" because the parent is null");
        String parentPath = parent.getPath();

        // remove any trailing slash (or sole-slash if the root)
        if (parentPath.endsWith("/")) parentPath = parentPath.substring(0, parentPath.length() - 1);
        String path = parentPath + "/" + name;

        return createNodeResource(path, properties != null ? properties : Collections.<String, Object>emptyMap());
    }


    private Resource createNodeResource(String path, Map<String, Object> properties) {
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


    private NodeImpl createNode(String path, Map<String, Object> properties) throws ItemNotFoundException, ItemExistsException {
        NodeImpl node = new NodeImpl((SessionImpl)session, path);

        for (String propName : properties.keySet()) {
            Object mapVal = properties.get(propName);
            setNodeProperty(node, propName, mapVal);
        }
        return node;
    }


    private static void setNodeProperty(NodeImpl node, String propName, Object propertyVal) {
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
    public @Nullable String getParentResourceType(@Nullable Resource resource) {
        if (resource == null) return null;
        val parent = resource.getParent();
        if (parent == null) return null;
        return parent.getResourceType();
    }


    @Override
    public @Nullable String getParentResourceType(@Nullable String s) {
        return getParentResourceType(resolve(s));
    }


    @Override
    public boolean isResourceType(@Nullable Resource resource, @Nullable String resourceType) {
        return resource != null && resourceType != null && resource.isResourceType(resourceType);
    }


    @Override
    public void refresh() {
    }


    @Override
    @SuppressWarnings({"unchecked", "TypeParameterExplicitlyExtendsObject"})
    public <AdapterType extends @NonNull Object> @Nullable AdapterType adaptTo(Class<AdapterType> type) {
        if (type.equals(Session.class)) return (AdapterType)session;
        if (type.equals(PageManager.class)) return (AdapterType)new PageManagerImpl(this);
        else return null;
    }


    private Resource constructResource(Item item) {
        return (item.isNode()) ? new NodeResourceImpl(this, (Node)item) : new PropertyResourceImpl(this, (Property)item);
    }

}

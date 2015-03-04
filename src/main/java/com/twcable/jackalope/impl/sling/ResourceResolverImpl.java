package com.twcable.jackalope.impl.sling;

import com.day.cq.wcm.api.PageManager;
import com.twcable.jackalope.impl.common.Paths;
import com.twcable.jackalope.impl.cq.PageManagerImpl;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;
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
            this.session = repository.loginAdministrative(null);
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
        return null;
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
    }


    @Override
    public Resource create(Resource resource, String s, Map<String, Object> stringObjectMap) throws PersistenceException {
        return null;
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
        return null;
    }


    @Override
    public String getParentResourceType(String s) {
        return null;
    }


    @Override
    public boolean isResourceType(Resource resource, String s) {
        return false;
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

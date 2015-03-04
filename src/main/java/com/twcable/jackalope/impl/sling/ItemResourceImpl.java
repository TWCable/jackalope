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

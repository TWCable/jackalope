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
}

package com.twcable.jackalope.impl.cq;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.twcable.jackalope.impl.sling.NodeResourceImpl;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Node;
import java.io.InputStream;

/**
 * Simple implementation of a {@link Rendition} and an asset resource
 */
class RenditionImpl extends NodeResourceImpl implements Rendition {
    private final Asset asset;


    public RenditionImpl(Asset asset, Resource resource) {
        super(resource.getResourceResolver(), resource.adaptTo(Node.class));
        this.asset = asset;
    }


    @Override
    public String getMimeType() {
        return null;
    }


    @Override
    public ValueMap getProperties() {
        return ResourceUtil.getValueMap(this.getChild("jcr:content"));
    }


    @Override
    public long getSize() {
        return 0;
    }


    @Override
    public InputStream getStream() {
        return null;
    }


    @Override
    public Asset getAsset() {
        return asset;
    }

}

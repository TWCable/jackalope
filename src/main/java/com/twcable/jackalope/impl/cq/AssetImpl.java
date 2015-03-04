package com.twcable.jackalope.impl.cq;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.RenditionPicker;
import com.day.cq.dam.api.Revision;
import org.apache.sling.api.resource.Resource;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Simple implementation of an {@link Asset}
 */
public class AssetImpl implements Asset {
    private final Resource resource;


    public AssetImpl(Resource resource) {
        this.resource = resource;
    }


    @Override
    public String getPath() {
        return resource.getPath();
    }


    @Override
    public String getName() {
        return resource.getName();
    }


    @Override
    public long getLastModified() {
        return System.currentTimeMillis();
    }


    @Override
    public Rendition getRendition(String s) {
        return null;
    }


    @Override
    public Rendition getOriginal() {
        return new RenditionImpl(this, resource.getChild("jcr:content/renditions/original"));
    }


    @Override
    public Rendition getCurrentOriginal() {
        return getOriginal();
    }


    @Override
    public boolean isSubAsset() {
        return false;
    }


    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }


    @Override
    public Resource setRendition(String s, InputStream inputStream, String s2) {
        return null;
    }


    @Override
    public void setCurrentOriginal(String s) {

    }


    @Override
    public Revision createRevision(String s, String s2) throws Exception {
        return null;
    }


    @Override
    public List<Rendition> getRenditions() {
        return null;
    }


    @Override
    public Iterator<Rendition> listRenditions() {
        return null;
    }


    @Override
    public Rendition getRendition(RenditionPicker renditionPicker) {
        return null;
    }


    @Override
    public String getModifier() {
        return null;
    }


    @Override
    public Asset restore(String s) throws Exception {
        return null;
    }


    @Override
    public Collection<Revision> getRevisions(Calendar calendar) throws Exception {
        return null;
    }


    @Override
    public String getMimeType() {
        return null;
    }


    @Override
    public Rendition addRendition(String s, InputStream inputStream, String s2) {
        return null;
    }


    @Override
    public Rendition addRendition(String s, InputStream inputStream, Map<String, Object> stringObjectMap) {
        return null;
    }


    @Override
    public Asset addSubAsset(String s, String s2, InputStream inputStream) {
        return null;
    }


    @Override
    public Collection<Asset> getSubAssets() {
        return null;
    }


    @Override
    public void removeRendition(String s) {

    }


    @Override
    public void setBatchMode(boolean b) {

    }


    @Override
    public boolean isBatchMode() {
        return false;
    }


    @Override
    public String getMetadataValue(String s) {
        return null;
    }


    @Override
    public Object getMetadata(String s) {
        return null;
    }


    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> adapterTypeClass) {
        return null;
    }
}

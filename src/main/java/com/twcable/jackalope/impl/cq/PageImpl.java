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

package com.twcable.jackalope.impl.cq;

import com.day.cq.commons.Filter;
import com.day.cq.commons.LabeledResource;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;
import com.day.text.Text;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Node;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

/**
 * Page implementation.
 */
public class PageImpl implements Page {

    private final Resource resource;
    private Tag[] tagCache;


    public PageImpl(Resource resource) {
        this.resource = resource;
    }


    @Override
    public String getPath() {
        return this.resource != null ? this.resource.getPath() : null;
    }


    @Override
    public PageManager getPageManager() {
        return resource != null ? resource.getResourceResolver().adaptTo(PageManager.class) : null;
    }


    @Override
    public Resource getContentResource() {
        return resource != null ? resource.getChild("jcr:content") : null;
    }


    @Override
    public Resource getContentResource(String s) {
        if (s == null || s.isEmpty()) return getContentResource();
        if (s.startsWith("/")) throw new IllegalArgumentException("Relative path expected.");
        return resource.getChild("jcr:content/" + s);
    }


    @Override
    public Iterator<Page> listChildren() {
        return listChildren(new PageFilter());
    }


    @Override
    public Iterator<Page> listChildren(Filter<Page> pageFilter) {
        return listChildren(pageFilter, false);
    }


    @Override
    public Iterator<Page> listChildren(Filter<Page> pageFilter, boolean deep) {
        return new PageIteratorImpl(resource, pageFilter, deep);
    }


    @Override
    public boolean hasChild(String s) {
        return resource != null && resource.getChild(s) != null;
    }


    @Override
    public int getDepth() {
        throw new UnsupportedOperationException();
    }


    @Override
    public Page getParent() {
        return getParent(1);
    }


    @Override
    public Page getParent(int i) {
        if (i == 0) return this;
        String path = Text.getRelativeParent(resource.getPath(), i);
        if (!path.isEmpty() && !path.equals("/")) return null;
        Resource parent = resource.getResourceResolver().getResource(path);
        return parent != null ? parent.adaptTo(Page.class) : null;
    }


    @Override
    public Page getAbsoluteParent(int i) {
        String path = Text.getAbsoluteParent(resource.getPath(), i);
        Resource parent = resource.getResourceResolver().getResource(path);
        return parent == null ? null : parent.adaptTo(Page.class);
    }


    @Override
    public ValueMap getProperties() {
        Resource contentResource = getContentResource();
        return contentResource != null ? contentResource.adaptTo(ValueMap.class) : null;
    }


    @Override
    public ValueMap getProperties(String s) {
        throw new UnsupportedOperationException();
    }


    @Override
    public String getName() {
        return resource != null ? resource.getName() : null;
    }


    @Override
    public String getTitle() {
        return getProperties().get("jcr:title", String.class);
    }


    @Override
    public String getDescription() {
        return getProperties().get("jcr:description", String.class);
    }


    @Override
    public String getPageTitle() {
        return getProperties().get("pageTitle", String.class);
    }


    @Override
    public String getNavigationTitle() {
        return getProperties().get("navTitle", String.class);
    }


    @Override
    public boolean isHideInNav() {
        ValueMap props = getProperties();
        return props.containsKey("hideInNav") ? props.get("hideInNav", Boolean.class) : false;
    }


    @Override
    public boolean hasContent() {
        return resource != null && resource.getChild("jcr:content") != null;
    }


    @Override
    public boolean isValid() {
        return timeUntilValid() == 0;
    }


    @Override
    public long timeUntilValid() {
        if (!hasContent()) return Long.MIN_VALUE;

        Calendar onTime = getOnTime(), offTime = getOffTime();
        long now = System.currentTimeMillis();
        long on = onTime != null ? onTime.getTimeInMillis() : Long.MIN_VALUE;
        long off = offTime != null ? offTime.getTimeInMillis() : Long.MAX_VALUE;
        return now < on ? on - now :
            now >= off ? off - now :
                0;
    }


    @Override
    public Calendar getOnTime() {
        ValueMap properties = getProperties();
        return properties != null ? properties.get("onTime", Calendar.class) : null;
    }


    @Override
    public Calendar getOffTime() {
        ValueMap properties = getProperties();
        return properties != null ? properties.get("offTime", Calendar.class) : null;
    }


    @Override
    public String getLastModifiedBy() {
        ValueMap properties = getProperties();
        String lastModifiedBy = properties.get("cq:lastModifiedBy", String.class);
        return lastModifiedBy != null ? lastModifiedBy : properties.get("jcr:lastModifiedBy", String.class);
    }


    @Override
    public Calendar getLastModified() {
        ValueMap properties = getProperties();
        Calendar lastModified = properties.get("cq:lastModified", Calendar.class);
        return lastModified != null ? lastModified : properties.get("jcr:lastModified", Calendar.class);
    }


    @Override
    public String getVanityUrl() {
        return getProperties().get("vanityUrl", String.class);
    }


    @Override
    public com.day.cq.tagging.Tag[] getTags() {
        if (tagCache != null) return tagCache;

        Resource contentResource = getContentResource();
        TagManager tagManager = contentResource.getResourceResolver().adaptTo(TagManager.class);
        tagCache = tagManager == null ? new Tag[0] : tagManager.getTags(contentResource);
        return tagCache;
    }


    @Override
    public void lock() throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean isLocked() {
        throw new UnsupportedOperationException();
    }


    @Override
    public String getLockOwner() {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean canUnlock() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void unlock() throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Template getTemplate() {
        throw new UnsupportedOperationException();
    }


    @Override
    public Locale getLanguage(boolean ignoreContent) {
        throw new UnsupportedOperationException();
    }


    @Override
    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        if (resource == null) return null;
        if (type.equals(Resource.class)) return (AdapterType)resource;
        if (type.equals(LabeledResource.class)) return (AdapterType)this;
        if (type.equals(Node.class)) return (AdapterType)resource.adaptTo(Node.class);
        return resource.adaptTo(type);
    }
}

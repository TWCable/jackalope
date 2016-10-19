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
package com.twcable.jackalope.impl.cq;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Revision;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.msm.Blueprint;
import com.google.common.collect.Lists;
import com.twcable.jackalope.JcrConstants;
import com.twcable.jackalope.impl.common.Paths;
import com.twcable.jackalope.impl.jcr.ValueImpl;
import com.twcable.jackalope.impl.sling.NodeResourceImpl;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Calendar;
import java.util.Collection;

/**
 * Page Manager implementation.
 */
@SuppressWarnings("RedundantCast")
public class PageManagerImpl implements PageManager {
    private final ResourceResolver resolver;
    private final Session session;


    public PageManagerImpl(ResourceResolver resolver) {
        this.resolver = resolver;
        Session session = resolver.adaptTo(Session.class);
        if (session == null) throw new IllegalArgumentException("Resolver must be adaptable to Session.");
        this.session = session;
    }


    @Override
    public @Nullable Page getPage(@Nullable String path) {
        if (path == null) return null;
        Resource resource = resolver.getResource(path);
        return resource != null ? resource.adaptTo(Page.class) : null;
    }


    @Override
    public @Nullable Page getContainingPage(@Nullable Resource resource) {
        if (resource == null) return null;

        String path = resource.getPath();
        while (!Paths.isRoot(path)) {
            Page page = getPage(path);
            if (page != null) return page;
            path = path.substring(0, path.lastIndexOf("/"));
        }

        return null;
    }


    @Override
    public @Nullable Page getContainingPage(@Nullable String path) {
        if (path == null) return null;
        return getContainingPage(resolver.getResource(path));
    }


    @Override
    public Page create(String parentPath, @Nullable String pageName, @Nullable String template, @Nullable String title) throws WCMException {
        return create(parentPath, pageName, template, title, true);
    }


    @Override
    public Page create(String parentPath, @Nullable String pageName, @Nullable String template, @Nullable String title,
                       boolean autoSave) throws WCMException {
        if (parentPath == null) throw new IllegalArgumentException("Parent path can't be null.");
        if (pageName == null && title == null)
            throw new IllegalArgumentException("Page and title name can't be both null.");
        if (template != null && !template.isEmpty())
            throw new UnsupportedOperationException("Templates are not supported.");

        try {
            Node parent = JcrUtils.getOrCreateByPath(parentPath, JcrConstants.NT_UNSTRUCTURED, session);

            if (pageName == null || pageName.isEmpty())
                pageName = JcrUtil.createValidName((@NonNull String)title, JcrUtil.HYPHEN_LABEL_CHAR_MAPPING);
            if (!JcrUtil.isValidName(pageName)) throw new IllegalArgumentException("Illegal page name: " + pageName);

            Node pageNode = parent.addNode(pageName, JcrConstants.CQ_PAGE);
            Node contentNode = pageNode.addNode("jcr:content", JcrConstants.CQ_PAGE_CONTENT);

            if (title != null && !title.isEmpty()) contentNode.setProperty("jcr:title", title);
            if (autoSave) {
                session.save();
            }

            return (@NonNull Page)getPage(pageNode.getPath());
        }
        catch (RepositoryException e) {
            throw new WCMException("Unable to create page", e);
        }
    }


    @Override
    public Page move(Page page, String destination, @Nullable String beforeName, boolean shallow, boolean resolveConflicts,
                     String[] adjustRefs) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Resource move(Resource resource, String destination, @Nullable String beforeName, boolean shallow, boolean resolveConflicts, String[] adjustRefs) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Page copy(Page page, String destination, @Nullable String beforeName, boolean shallow, boolean resolveConflict) throws WCMException {
        return copy(page, destination, beforeName, shallow, resolveConflict, false);
    }


    @Override
    @SuppressWarnings("ConstantConditions")
    public Page copy(Page page, String destination, @Nullable String beforeName, boolean shallow, boolean resolveConflict, boolean autoSave) throws WCMException {
        if (beforeName != null && !beforeName.isEmpty())
            throw new UnsupportedOperationException("Ordering not supported.");
        if (resolveConflict) throw new UnsupportedOperationException("Conflict resolution not supported.");
        if (!shallow) throw new UnsupportedOperationException("Recursive page copy not supported.");

        Page newPage = null;
        try {
            newPage = create(destination, page.getName(), null, page.getTitle());
            Resource contentResource = newPage.getContentResource();
            if (contentResource == null) throw new WCMException("Content resource is null.");

            Node contentNode = contentResource.adaptTo(Node.class);
            ValueMap properties = newPage.getProperties();
            if (contentNode != null && properties != null)
                for (String key : properties.keySet()) contentNode.setProperty(key, new ValueImpl(properties.get(key)));
            else throw new WCMException("Unable to copy properties");

            final Resource pageContentResource = page.getContentResource();
            if (pageContentResource != null) {
                for (Resource child : Lists.newArrayList(pageContentResource.listChildren())) {
                    copy(child, contentResource.getPath() + "/" + child.getName(), null, false, false, false);
                }
            }

            if (autoSave) session.save();
            return newPage;
        }
        catch (RepositoryException e) {
            if (newPage != null)
                delete(newPage, false);
            throw new WCMException("Unable to copy properties.", e);
        }
        catch (WCMException e) {
            if (newPage != null)
            delete(newPage, false);
            throw e;
        }
    }


    @Override
    public Resource copy(Resource resource, String destination, String beforeName, boolean shallow, boolean resolveConflict) throws WCMException {
        return copy(resource, destination, beforeName, shallow, resolveConflict, false);
    }


    @Override
    public Resource copy(Resource resource, String destination, @Nullable String beforeName, boolean shallow, boolean resolveConflict, boolean autoSave) throws WCMException {
        if (beforeName != null && !beforeName.isEmpty())
            throw new UnsupportedOperationException("Ordering not supported.");
        if (resolveConflict) throw new UnsupportedOperationException("Conflict resolution not supported.");

        try {
            int index = destination.lastIndexOf("/");
            String parentPath = destination.substring(0, index);
            String name = destination.substring(index + 1);
            Node parent = JcrUtils.getOrCreateByPath(parentPath, JcrConstants.NT_UNSTRUCTURED, session);

            Node node = resource.adaptTo(Node.class);
            if (node == null) throw new WCMException("Unable to get source node.");
            if (node.hasNode(name)) throw new WCMException("Node already exists at destination.");
            Node newNode = parent.addNode(name, node.getPrimaryNodeType().toString());

            ValueMap properties = resource.adaptTo(ValueMap.class);
            if (properties != null)
                for (String key : properties.keySet()) newNode.setProperty(key, new ValueImpl(properties.get(key)));

            if (!shallow && node.hasNodes())
                for (Resource child : Lists.newArrayList(resource.listChildren()))
                    copy(child, resource.getPath() + "/" + child.getName(), null, false, false, false);

            if (autoSave) session.save();
            return new NodeResourceImpl(resolver, newNode);

        }
        catch (RepositoryException e) {
            try {
                if (session.itemExists(destination)) session.removeItem(destination);
            }
            catch (RepositoryException e1) {
                e1.printStackTrace();
            }

            throw new WCMException("Unable to copy resource.", e);
        }
        catch (WCMException e) {
            try {
                if (session.itemExists(destination)) session.removeItem(destination);
            }
            catch (RepositoryException e1) {
                e1.printStackTrace();
            }

            throw e;
        }
    }


    @Override
    public void delete(Page page, boolean shallow) throws WCMException {
        delete(page, shallow, true);
    }


    @Override
    public void delete(Page page, boolean shallow, boolean autoSave) throws WCMException {
        if (page == null) return;

        if (!shallow) {
            final Resource resource = page.adaptTo(Resource.class);
            if (resource != null)
                delete(resource, false, autoSave);
        }
        else {
            final Resource resource = page.getContentResource();
            if (resource != null)
                delete(resource, true, autoSave);
        }
    }


    @Override
    public void delete(Resource resource, boolean shallow) throws WCMException {
        delete(resource, shallow, true);
    }


    @Override
    public void delete(Resource resource, boolean shallow, boolean autoSave) throws WCMException {
        if (resource == null) return;

        Node node = resource.adaptTo(Node.class);
        if (node == null) return;

        try {
            session.removeItem(node.getPath());
            if (autoSave) {
                session.save();
            }
        }
        catch (RepositoryException e) {
            throw new WCMException("Could not delete resource.", e);
        }
    }


    @Override
    public void order(Page page, @Nullable String beforeName) throws WCMException {
        order(page, beforeName, true);
    }


    @Override
    public void order(@Nullable Page page, @Nullable String beforeName, boolean autoSave) throws WCMException {
        if (page == null) return;

        order(page.getContentResource(), beforeName, autoSave);
    }


    @Override
    public void order(@Nullable Resource resource, @Nullable String beforeName) throws WCMException {
        order(resource, beforeName, true);
    }


    @Override
    public void order(@Nullable Resource resource, @Nullable String beforeName, boolean autoSave) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Template getTemplate(String s) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Collection<Template> getTemplates(@Nullable String s) {
        throw new UnsupportedOperationException();
    }


    @Override
    @SuppressWarnings("deprecation")
    public Collection<Blueprint> getBlueprints(@Nullable String s) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Revision createRevision(Page page) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Revision createRevision(Page page, String s, String s1) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Collection<Revision> getRevisions(String s, @Nullable Calendar calendar) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Collection<Revision> getRevisions(String s, @Nullable Calendar calendar, boolean b) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Collection<Revision> getChildRevisions(String s, @Nullable Calendar calendar) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Collection<Revision> getChildRevisions(String s, @Nullable Calendar calendar, boolean b) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Collection<Revision> getChildRevisions(String s, String s1, @Nullable Calendar calendar) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Page restore(String s, String s1) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Page restoreTree(String s, Calendar calendar) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Page restoreTree(String s, Calendar calendar, boolean b) throws WCMException {
        throw new UnsupportedOperationException();
    }


    @Override
    public void touch(Node node, boolean b, @Nullable Calendar calendar, boolean b1) throws WCMException {
        throw new UnsupportedOperationException();
    }
}

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
package com.twcable.jackalope.impl.jcr;

import com.twcable.jackalope.impl.common.Paths;
import lombok.val;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.jcr.Credentials;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.retention.RetentionManager;
import javax.jcr.security.AccessControlManager;
import javax.jcr.version.VersionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple implementation of an {@link Session}
 */
@SuppressWarnings({"DuplicateThrows", "RedundantCast", "SimplifiableIfStatement"})
public class SessionImpl implements Session {
    private final Repository repository;
    private final Map<String, ItemImpl> itemStore = new LinkedHashMap<>();
    private boolean isLive = true;

    private Set<String> addedItems = new LinkedHashSet<>();
    private Set<String> changedItems = new LinkedHashSet<>();

    private @Nullable Workspace workspace = null;


    @Override
    public Repository getRepository() {
        return repository;
    }


    @Override
    public @Nullable String getUserID() {
        return null;
    }


    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }


    @Override
    public @Nullable Object getAttribute(@Nullable String name) {
        return null;
    }


    @Override
    public Workspace getWorkspace() {
        if (workspace == null) {
            WorkspaceImpl workspaceImpl = new WorkspaceImpl();
            workspaceImpl.setSession(this);
            workspace = workspaceImpl;
        }
        return workspace;
    }


    @Override
    public Node getRootNode() throws RepositoryException {
        final Node node = (Node)itemStore.get("/");
        if (node != null)
            return node; // Added in ctor
        throw new RepositoryException("Could not get root node");
    }


    @Override
    public Session impersonate(@Nullable Credentials credentials) throws LoginException, RepositoryException {
        return this;
    }


    @Override
    public Node getNodeByUUID(@Nullable String uuid) throws ItemNotFoundException, RepositoryException {
        throw new ItemNotFoundException();  //TODO: Implement IDs
    }


    @Override
    public Node getNodeByIdentifier(@Nullable String id) throws ItemNotFoundException, RepositoryException {
        throw new ItemNotFoundException();  //TODO: Implement IDs
    }


    @Override
    public Item getItem(@Nullable String absPath) throws PathNotFoundException {
        if (!itemExists(absPath)) throw new PathNotFoundException((@NonNull String)absPath);
        val itemImpl = getItemImpl((@NonNull String)absPath);
        if (itemImpl == null) throw new PathNotFoundException((@NonNull String)absPath);
        return itemImpl;
    }


    @Override
    public Node getNode(@Nullable String absPath) throws PathNotFoundException {
        return (Node)getItem(absPath);
    }


    @Override
    public Property getProperty(@Nullable String absPath) throws PathNotFoundException {
        return (Property)getItem(absPath);
    }


    @Override
    public boolean itemExists(@Nullable String absPath) {
        if (absPath == null) return false;
        return itemStore.containsKey(absPath);
    }


    @Override
    public boolean nodeExists(@Nullable String absPath) {
        if (absPath == null) return false;
        return itemStore.containsKey(absPath) && itemStore.get(absPath).isNode();
    }


    @Override
    public boolean propertyExists(@Nullable String absPath) {
        if (absPath == null) return false;
        return itemStore.containsKey(absPath) && !itemStore.get(absPath).isNode();
    }


    @Override
    public void move(@Nullable String srcAbsPath, @Nullable String destAbsPath) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException {
        if (!nodeExists(Paths.parent(srcAbsPath))) throw new PathNotFoundException();
        if (!nodeExists(Paths.parent(destAbsPath))) throw new PathNotFoundException();
        if (itemExists(destAbsPath)) throw new ItemExistsException();

        // Can't modify a map while iterating over it.  So, put the keys that need to me moved into a list while
        // iterating over the map and then iterate over the list
        List<String> keys = new ArrayList<>();

        for (String key : itemStore.keySet())
            if (key.startsWith((@NonNull String)srcAbsPath))
                keys.add(key);

        for (String key : keys)
            moveItem(key, key.replaceFirst("^" + srcAbsPath, (@NonNull String)destAbsPath));
    }


    @Override
    public void removeItem(@Nullable String absPath) throws RepositoryException {
        if (absPath != null && itemExists(absPath)) {
            val itemImpl = getItemImpl(absPath);
            if (itemImpl == null) throw new ItemNotFoundException(absPath);
            removeItem(itemImpl);
        }
    }


    @Override
    public void save() {
        addedItems.clear();
        changedItems.clear();
    }


    @Override
    public void refresh(boolean keepChanges) throws RepositoryException {
    }


    @Override
    public boolean hasPendingChanges() {
        return !addedItems.isEmpty() || !changedItems.isEmpty();
    }


    @Override
    public ValueFactory getValueFactory() throws UnsupportedRepositoryOperationException, RepositoryException {
        return new ValueFactoryImpl();
    }


    @Override
    public boolean hasPermission(@Nullable String absPath, @Nullable String actions) throws RepositoryException {
        return false;
    }


    @Override
    public void checkPermission(@Nullable String absPath, @Nullable String actions) throws AccessControlException, RepositoryException {
    }


    @Override
    public boolean hasCapability(@Nullable String methodName, @Nullable Object target, Object[] arguments) throws RepositoryException {
        return false;
    }


    @Override
    public ContentHandler getImportContentHandler(@Nullable String parentAbsPath, int uuidBehavior) throws PathNotFoundException, ConstraintViolationException, VersionException, LockException, RepositoryException {
        throw new RepositoryException("Not implemented");
    }


    @Override
    public void importXML(@Nullable String parentAbsPath, @Nullable InputStream in, int uuidBehavior) throws IOException, PathNotFoundException, ItemExistsException, ConstraintViolationException, VersionException, InvalidSerializedDataException, LockException, RepositoryException {
    }


    @Override
    public void exportSystemView(@Nullable String absPath, @Nullable ContentHandler contentHandler, boolean skipBinary, boolean noRecurse) throws PathNotFoundException, SAXException, RepositoryException {
    }


    @Override
    public void exportSystemView(@Nullable String absPath, @Nullable OutputStream out, boolean skipBinary, boolean noRecurse) throws IOException, PathNotFoundException, RepositoryException {
    }


    @Override
    public void exportDocumentView(@Nullable String absPath, @Nullable ContentHandler contentHandler, boolean skipBinary, boolean noRecurse) throws PathNotFoundException, SAXException, RepositoryException {
    }


    @Override
    public void exportDocumentView(@Nullable String absPath, @Nullable OutputStream out, boolean skipBinary, boolean noRecurse) throws IOException, PathNotFoundException, RepositoryException {
    }


    @Override
    public void setNamespacePrefix(@Nullable String prefix, @Nullable String uri) throws NamespaceException, RepositoryException {
    }


    @Override
    public String[] getNamespacePrefixes() throws RepositoryException {
        return new String[0];
    }


    @Override
    public String getNamespaceURI(@Nullable String prefix) throws NamespaceException, RepositoryException {
        throw new NamespaceException("Not implemented");
    }


    @Override
    public String getNamespacePrefix(@Nullable String uri) throws NamespaceException, RepositoryException {
        throw new NamespaceException("Not implemented");
    }


    @Override
    public void logout() {
        isLive = false;
    }


    @Override
    public boolean isLive() {
        return isLive;
    }


    @Override
    public void addLockToken(@Nullable String lt) {
    }


    @Override
    public String[] getLockTokens() {
        return new String[0];
    }


    @Override
    public void removeLockToken(@Nullable String lt) {
    }


    @Override
    public AccessControlManager getAccessControlManager() throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public RetentionManager getRetentionManager() throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    //
    // The following methods are package private and implement the functionality used to implement the various
    // features of the jackrabbit project.
    //

    Item addItem(ItemImpl item) throws ItemNotFoundException, ItemExistsException {
        if (itemStore.containsKey(item.getPath())) throw new ItemExistsException();
        addedItems.add(item.getPath());
        return storeItem(item);
    }


    Item changeItem(ItemImpl item) throws ItemNotFoundException {
        if (!addedItems.contains(item.getPath()))
            changedItems.add(item.getPath());
        return storeItem(item);
    }


    Item removeItem(ItemImpl item) throws ItemNotFoundException {
        val parentImpl = item.getParentImpl();
        if (parentImpl == null) throw new ItemNotFoundException("No parent for " + item);
        changedItems.add(parentImpl.getPath());
        for (ItemImpl descendant : getDescendants(item))
            itemStore.remove(descendant.getPath());
        itemStore.remove(item.getPath());
        return item;
    }


    private @Nullable ItemImpl getItemImpl(String absPath) {
        return itemStore.get(absPath);
    }


    private Item storeItem(ItemImpl item) throws ItemNotFoundException {
        itemStore.put(item.getPath(), item);
        return item;
    }


    private void moveItem(String src, String dest) throws PathNotFoundException {
        val item = itemStore.get(src);
        if (item == null) throw new PathNotFoundException(src);
        item.setPath(dest);
        itemStore.put(dest, item);
        itemStore.remove(src);
    }


    private List<ItemImpl> getDescendants(ItemImpl item) {
        List<ItemImpl> descendants = new ArrayList<>();
        for (String key : itemStore.keySet())
            if (Paths.ancestorOf(item.getPath(), key))
                descendants.add(itemStore.get(key));
        return descendants;
    }


    List<Item> getChildren(Item parent) {
        List<Item> children = new ArrayList<>();
        for (ItemImpl item : itemStore.values())
            if (item.getParentImpl() == parent)
                children.add(item);
        return children;
    }


    public SessionImpl() {
        this(new RepositoryImpl());
    }


    @SuppressWarnings({"method.invocation.invalid", "argument.type.incompatible"})
    public SessionImpl(Repository repository) {
        this.repository = repository;
        try {
            addItem(new NodeImpl(this, "/"));
        }
        catch (RepositoryException re) { /* can't happen */ }
        save();
    }


    void save(ItemImpl item) {
        String path = item.getPath();
        for (Iterator<String> i = addedItems.iterator(); i.hasNext(); )
            if (Paths.selfOrAncestorOf(path, i.next()))
                i.remove();
        for (Iterator<String> i = changedItems.iterator(); i.hasNext(); )
            if (Paths.selfOrAncestorOf(path, i.next()))
                i.remove();
    }


    boolean isNew(ItemImpl item) {
        return addedItems.contains(item.getPath());
    }


    boolean isModified(ItemImpl item) {
        return changedItems.contains(item.getPath());
    }
}

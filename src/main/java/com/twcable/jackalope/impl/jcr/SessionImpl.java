package com.twcable.jackalope.impl.jcr;

import com.twcable.jackalope.impl.common.Paths;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple implementation of an {@link Session}
 */
@SuppressWarnings("DuplicateThrows")
public class SessionImpl implements Session {
    private final Repository repository;
    private final Map<String, ItemImpl> itemStore = new HashMap<>();
    private boolean isLive = true;

    private Set<String> addedItems = new HashSet<>();
    private Set<String> changedItems = new HashSet<>();

    private Workspace workspace = null;


    @Override
    public Repository getRepository() {
        return repository;
    }


    @Override
    public String getUserID() {
        return null;
    }


    @Override
    public String[] getAttributeNames() {
        return new String[0];
    }


    @Override
    public Object getAttribute(String name) {
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
    public Node getRootNode() {
        return (Node)itemStore.get("/"); // Added in ctor
    }


    @Override
    public Session impersonate(Credentials credentials) throws LoginException, RepositoryException {
        return this;
    }


    @Override
    public Node getNodeByUUID(String uuid) throws ItemNotFoundException, RepositoryException {
        return null;  //TODO: Implement IDs
    }


    @Override
    public Node getNodeByIdentifier(String id) throws ItemNotFoundException, RepositoryException {
        return null;  //TODO: Implement IDs
    }


    @Override
    public Item getItem(String absPath) throws PathNotFoundException {
        if (!itemExists(absPath)) throw new PathNotFoundException();
        return getItemImpl(absPath);
    }


    @Override
    public Node getNode(String absPath) throws PathNotFoundException {
        return (Node)getItem(absPath);
    }


    @Override
    public Property getProperty(String absPath) throws PathNotFoundException {
        return (Property)getItem(absPath);
    }


    @Override
    public boolean itemExists(String absPath) {
        return itemStore.containsKey(absPath);
    }


    @Override
    public boolean nodeExists(String absPath) {
        return itemExists(absPath) && itemStore.get(absPath).isNode();
    }


    @Override
    public boolean propertyExists(String absPath) {
        return itemExists(absPath) && !itemStore.get(absPath).isNode();
    }


    @Override
    public void move(String srcAbsPath, String destAbsPath) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException {
        if (!nodeExists(Paths.parent(srcAbsPath))) throw new PathNotFoundException();
        if (!nodeExists(Paths.parent(destAbsPath))) throw new PathNotFoundException();
        if (itemExists(destAbsPath)) throw new ItemExistsException();

        // Can't modify a map while iterating over it.  So, put the keys that need to me moved into a list while
        // iterating over the map and then iterate over the list
        List<String> keys = new ArrayList<>();

        for (String key : itemStore.keySet())
            if (key.startsWith(srcAbsPath))
                keys.add(key);

        for (String key : keys)
            moveItem(key, key.replaceFirst("^" + srcAbsPath, destAbsPath));
    }


    @Override
    public void removeItem(String absPath) {
        if (itemExists(absPath))
            removeItem(getItemImpl(absPath));
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
    public boolean hasPermission(String absPath, String actions) throws RepositoryException {
        return false;
    }


    @Override
    public void checkPermission(String absPath, String actions) throws AccessControlException, RepositoryException {
    }


    @Override
    public boolean hasCapability(String methodName, Object target, Object[] arguments) throws RepositoryException {
        return false;
    }


    @Override
    public ContentHandler getImportContentHandler(String parentAbsPath, int uuidBehavior) throws PathNotFoundException, ConstraintViolationException, VersionException, LockException, RepositoryException {
        return null;
    }


    @Override
    public void importXML(String parentAbsPath, InputStream in, int uuidBehavior) throws IOException, PathNotFoundException, ItemExistsException, ConstraintViolationException, VersionException, InvalidSerializedDataException, LockException, RepositoryException {
    }


    @Override
    public void exportSystemView(String absPath, ContentHandler contentHandler, boolean skipBinary, boolean noRecurse) throws PathNotFoundException, SAXException, RepositoryException {
    }


    @Override
    public void exportSystemView(String absPath, OutputStream out, boolean skipBinary, boolean noRecurse) throws IOException, PathNotFoundException, RepositoryException {
    }


    @Override
    public void exportDocumentView(String absPath, ContentHandler contentHandler, boolean skipBinary, boolean noRecurse) throws PathNotFoundException, SAXException, RepositoryException {
    }


    @Override
    public void exportDocumentView(String absPath, OutputStream out, boolean skipBinary, boolean noRecurse) throws IOException, PathNotFoundException, RepositoryException {
    }


    @Override
    public void setNamespacePrefix(String prefix, String uri) throws NamespaceException, RepositoryException {
    }


    @Override
    public String[] getNamespacePrefixes() throws RepositoryException {
        return new String[0];
    }


    @Override
    public String getNamespaceURI(String prefix) throws NamespaceException, RepositoryException {
        return null;
    }


    @Override
    public String getNamespacePrefix(String uri) throws NamespaceException, RepositoryException {
        return null;
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
    public void addLockToken(String lt) {
    }


    @Override
    public String[] getLockTokens() {
        return new String[0];
    }


    @Override
    public void removeLockToken(String lt) {
    }


    @Override
    public AccessControlManager getAccessControlManager() throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public RetentionManager getRetentionManager() throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    /* The following methods are package private and implement the functionality used to implement the various
     * features of the jackrabbit project.
     */
    Item addItem(@Nonnull ItemImpl item) throws ItemNotFoundException, ItemExistsException {
        if (itemStore.containsKey(item.getPath())) throw new ItemExistsException();
        addedItems.add(item.getPath());
        return storeItem(item);
    }


    Item changeItem(@Nonnull ItemImpl item) throws ItemNotFoundException {
        if (!addedItems.contains(item.getPath()))
            changedItems.add(item.getPath());
        return storeItem(item);
    }


    Item removeItem(@Nonnull ItemImpl item) {
        changedItems.add(item.getParentImpl().getPath());
        for (ItemImpl descendant : getDescendants(item))
            itemStore.remove(descendant.getPath());
        itemStore.remove(item.getPath());
        return item;
    }


    private ItemImpl getItemImpl(String absPath) {
        return itemStore.get(absPath);
    }


    private Item storeItem(@Nonnull ItemImpl item) throws ItemNotFoundException {
        itemStore.put(item.getPath(), item);
        return item;
    }


    private void moveItem(String src, String dest) {
        ItemImpl item = itemStore.get(src);
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
        this(null);
    }


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

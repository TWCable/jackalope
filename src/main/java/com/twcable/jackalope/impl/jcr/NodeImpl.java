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
import com.twcable.jackalope.impl.common.Values;
import org.apache.jackrabbit.value.ReferenceValue;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.Nonnull;
import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidLifecycleTransitionException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.MergeException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.ActivityViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


/**
 * Implementation of jcr Node interface.
 */
@SuppressWarnings("DuplicateThrows")
public class NodeImpl extends ItemImpl implements Node {
    private final UUID uuid = UUID.randomUUID();
    private static final String DEFAULT_NODETYPE = NodeType.NT_UNSTRUCTURED;


    /**
     * Construct a new NodeImpl.
     */
    public NodeImpl(@Nonnull SessionImpl session, String path) throws ItemNotFoundException, ItemExistsException {
        super(session, path);
    }


    @Override
    public Node addNode(String relPath) throws ItemExistsException, PathNotFoundException, VersionException, ConstraintViolationException, LockException, RepositoryException {
        return addNode(relPath, DEFAULT_NODETYPE);
    }


    @Override
    public Node addNode(String relPath, String primaryNodeTypeName) throws ItemExistsException, PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException, RepositoryException {
        Node node = new NodeImpl(session, Paths.resolve(getPath(), relPath));
        node.setPrimaryType(primaryNodeTypeName);
        session.changeItem(this);
        return node;
    }


    @Override
    public void orderBefore(String srcChildRelPath, @Nullable String destChildRelPath) throws UnsupportedRepositoryOperationException, VersionException, ConstraintViolationException, ItemNotFoundException, LockException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public @Nullable Property setProperty(String name, @Nullable Value value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        if (value == null) {
            if (hasProperty(name))
                getProperty(name).remove();
            return null;
        }
        PropertyImpl property = getOrCreateProperty(name);
        property.setValue(value);
        session.changeItem(this);
        return property;
    }


    @Override
    public @Nullable Property setProperty(String name, @Nullable Value value, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, value); // TODO: Implement type conversions
    }


    @Override
    public @Nullable Property setProperty(String name, Value[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        PropertyImpl property = getOrCreateProperty(name);
        property.setValue(values);
        session.changeItem(this);
        return property;
    }


    @Override
    public @Nullable Property setProperty(String name, Value[] values, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, values); // TODO: Implement type conversions
    }


    @Override
    public @Nullable Property setProperty(String name, String[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, Values.convertStringsToValues(values));
    }


    @Override
    public @Nullable Property setProperty(String name, String[] values, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, values); // TODO: Implement type conversions
    }


    @Override
    public @Nullable Property setProperty(String name, String value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public Property setProperty(String name, String value, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO: Implement type conversions
        throw new UnsupportedOperationException();
    }


    @Override
    public @Nullable Property setProperty(String name, InputStream value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return null;  //deprecated
    }


    @Override
    public @Nullable Property setProperty(String name, Binary value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public @Nullable Property setProperty(String name, boolean value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public @Nullable Property setProperty(String name, double value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public @Nullable Property setProperty(String name, BigDecimal value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public @Nullable Property setProperty(String name, long value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public @Nullable Property setProperty(String name, Calendar value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public @Nullable Property setProperty(String name, Node value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ReferenceValue(value));
    }


    @Override
    public Node getNode(String relPath) throws PathNotFoundException, RepositoryException {
        return session.getNode(Paths.resolve(getPath(), relPath));
    }


    @Override
    public NodeIterator getNodes() throws RepositoryException {
        List<Node> children = new ArrayList<>();
        for (Item item : session.getChildren(this))
            if (item.isNode())
                children.add((Node)item);
        return new NodeIteratorImpl(children);
    }


    @Override
    public NodeIterator getNodes(String namePattern) throws RepositoryException {
        // TODO: Implement this soon
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public NodeIterator getNodes(String[] nameGlobs) throws RepositoryException {
        // TODO: Implement this soon
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public Property getProperty(String relPath) throws PathNotFoundException, RepositoryException {
        return session.getProperty(Paths.resolve(getPath(), relPath));
    }


    @Override
    public PropertyIterator getProperties() throws RepositoryException {
        List<Property> children = new ArrayList<>();
        for (Item item : session.getChildren(this))
            if (!item.isNode())
                children.add((Property)item);
        return new PropertyIteratorImpl(children);
    }


    @Override
    public PropertyIterator getProperties(String namePattern) throws RepositoryException {
        return new PropertyIteratorImpl(Collections.emptyList());  //Not implemented
    }


    @Override
    public PropertyIterator getProperties(String[] nameGlobs) throws RepositoryException {
        return new PropertyIteratorImpl(Collections.emptyList());  //Not implemented
    }


    @Override
    public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException {
        throw new ItemNotFoundException();  // TODO implemented
    }


    @Override
    public String getUUID() throws UnsupportedRepositoryOperationException, RepositoryException {
        return uuid.toString();
    }


    @Override
    public String getIdentifier() throws RepositoryException {
        return getPath();
    }


    @Override
    public int getIndex() throws RepositoryException {
        return 1;  //Do not support same name sibling nodes
    }


    @Override
    public PropertyIterator getReferences() throws RepositoryException {
        return new PropertyIteratorImpl(Collections.emptyList());  //Not implemented
    }


    @Override
    public PropertyIterator getReferences(String name) throws RepositoryException {
        return new PropertyIteratorImpl(Collections.emptyList());  //Not implemented
    }


    @Override
    public PropertyIterator getWeakReferences() throws RepositoryException {
        return new PropertyIteratorImpl(Collections.emptyList());  //Not implemented
    }


    @Override
    public PropertyIterator getWeakReferences(String name) throws RepositoryException {
        return new PropertyIteratorImpl(Collections.emptyList());  //Not implemented
    }


    @Override
    public boolean hasNode(String relPath) throws RepositoryException {
        return session.nodeExists(Paths.resolve(getPath(), relPath));
    }


    @Override
    public boolean hasProperty(String relPath) throws RepositoryException {
        return session.propertyExists(Paths.resolve(getPath(), relPath));
    }


    @Override
    public boolean hasNodes() throws RepositoryException {
        return getNodes().hasNext();
    }


    @Override
    public boolean hasProperties() throws RepositoryException {
        return getProperties().hasNext();
    }


    @Override
    public NodeType getPrimaryNodeType() throws RepositoryException {
        return new NodeTypeImpl(getProperty("jcr:primaryType").getString());
    }


    @Override
    public NodeType[] getMixinNodeTypes() throws RepositoryException {
        return new NodeType[0];
    }


    @Override
    public boolean isNodeType(String nodeTypeName) throws RepositoryException {
        return getProperty("jcr:primaryType").getString().equals(nodeTypeName);
    }


    @Override
    public void setPrimaryType(String nodeTypeName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
        setProperty("jcr:primaryType", nodeTypeName);
    }


    @Override
    public void addMixin(String mixinName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
        //Not Implemented
    }


    @Override
    public void removeMixin(String mixinName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
        //Not Implemented
    }


    @Override
    public boolean canAddMixin(String mixinName) throws NoSuchNodeTypeException, RepositoryException {
        return false;
    }


    @Override
    public NodeDefinition getDefinition() throws RepositoryException {
        return new NodeDefinitionImpl();
    }


    @Override
    public Version checkin() throws VersionException, UnsupportedRepositoryOperationException, InvalidItemStateException, LockException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public void checkout() throws UnsupportedRepositoryOperationException, LockException, ActivityViolationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public void doneMerge(Version version) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public void cancelMerge(Version version) throws VersionException, InvalidItemStateException, UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public void update(String srcWorkspace) throws NoSuchWorkspaceException, AccessDeniedException, LockException, InvalidItemStateException, RepositoryException {
        //Not Implemented
    }


    @Override
    public NodeIterator merge(String srcWorkspace, boolean bestEffort) throws NoSuchWorkspaceException, AccessDeniedException, MergeException, LockException, InvalidItemStateException, RepositoryException {
        return new NodeIteratorImpl(Collections.emptyList());  //deprecated
    }


    @Override
    public String getCorrespondingNodePath(String workspaceName) throws ItemNotFoundException, NoSuchWorkspaceException, AccessDeniedException, RepositoryException {
        return "";  // TODO not implemented
    }


    @Override
    public NodeIterator getSharedSet() throws RepositoryException {
        return new NodeIteratorImpl(Collections.emptyList());
    }


    @Override
    public void removeSharedSet() throws VersionException, LockException, ConstraintViolationException, RepositoryException {
        //Not Implemented.
    }


    @Override
    public void removeShare() throws VersionException, LockException, ConstraintViolationException, RepositoryException {
        //Not Implemented
    }


    @Override
    public boolean isCheckedOut() throws RepositoryException {
        return false;
    }


    @Override
    public void restore(String versionName, boolean removeExisting) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public void restore(Version version, boolean removeExisting) throws VersionException, ItemExistsException, InvalidItemStateException, UnsupportedRepositoryOperationException, LockException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public void restore(Version version, String relPath, boolean removeExisting) throws PathNotFoundException, ItemExistsException, VersionException, ConstraintViolationException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public void restoreByLabel(String versionLabel, boolean removeExisting) throws VersionException, ItemExistsException, UnsupportedRepositoryOperationException, LockException, InvalidItemStateException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public VersionHistory getVersionHistory() throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public Version getBaseVersion() throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public Lock lock(boolean isDeep, boolean isSessionScoped) throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public Lock getLock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public void unlock() throws UnsupportedRepositoryOperationException, LockException, AccessDeniedException, InvalidItemStateException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public boolean holdsLock() throws RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public boolean isLocked() throws RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public void followLifecycleTransition(String transition) throws UnsupportedRepositoryOperationException, InvalidLifecycleTransitionException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public String[] getAllowedLifecycleTransistions() throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public boolean isNode() {
        return true;
    }


    @Override
    public void accept(ItemVisitor visitor) throws RepositoryException {
        visitor.visit(this);
    }


    private PropertyImpl getOrCreateProperty(String name) throws RepositoryException {
        return hasProperty(name) ? (PropertyImpl)getProperty(name) : new PropertyImpl(session, Paths.resolve(getPath(), name));
    }
}

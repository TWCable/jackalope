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

package com.twcable.jackalope.impl.jcr;

import com.twcable.jackalope.impl.common.Paths;
import com.twcable.jackalope.impl.common.Values;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;

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
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.ActivityViolationException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;


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
    public void orderBefore(String srcChildRelPath, String destChildRelPath) throws UnsupportedRepositoryOperationException, VersionException, ConstraintViolationException, ItemNotFoundException, LockException, RepositoryException {
        throw new UnsupportedRepositoryOperationException();
    }


    @Override
    public Property setProperty(String name, Value value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
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
    public Property setProperty(String name, Value value, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, value); // TODO: Implement type conversions
    }


    @Override
    public Property setProperty(String name, Value[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        PropertyImpl property = getOrCreateProperty(name);
        property.setValue(values);
        session.changeItem(this);
        return property;
    }


    @Override
    public Property setProperty(String name, Value[] values, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, values); // TODO: Implement type conversions
    }


    @Override
    public Property setProperty(String name, String[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, Values.convertStringsToValues(values));
    }


    @Override
    public Property setProperty(String name, String[] values, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, values); // TODO: Implement type conversions
    }


    @Override
    public Property setProperty(String name, String value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public Property setProperty(String name, String value, int type) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        // TODO: Implement type conversions
        throw new UnsupportedOperationException();
    }


    @Override
    public Property setProperty(String name, InputStream value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return null;  //deprecated
    }


    @Override
    public Property setProperty(String name, Binary value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public Property setProperty(String name, boolean value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public Property setProperty(String name, double value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public Property setProperty(String name, BigDecimal value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public Property setProperty(String name, long value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public Property setProperty(String name, Calendar value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return setProperty(name, new ValueImpl(value));
    }


    @Override
    public Property setProperty(String name, Node value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        return null;  // Not implemented
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
        return null;  //Not implemented
    }


    @Override
    public PropertyIterator getProperties(String[] nameGlobs) throws RepositoryException {
        return null;  //Not implemented
    }


    @Override
    public Item getPrimaryItem() throws ItemNotFoundException, RepositoryException {
        return null;  //Not implemented
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
        return null;  //Not implemented
    }


    @Override
    public PropertyIterator getReferences(String name) throws RepositoryException {
        return null;  //Not implemented
    }


    @Override
    public PropertyIterator getWeakReferences() throws RepositoryException {
        return null;  //Not implemented
    }


    @Override
    public PropertyIterator getWeakReferences(String name) throws RepositoryException {
        return null;  //Not implemented
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
        return new NodeTypeImpl(primaryTypeProperty().getString());
    }


    private PropertyImpl primaryTypeProperty() throws RepositoryException {
        return getOrCreateProperty(getJcrNameForQName(Property.JCR_PRIMARY_TYPE));
    }


    private PropertyImpl mixinProperty() throws RepositoryException {
        return getOrCreateProperty(getJcrNameForQName(Property.JCR_MIXIN_TYPES));
    }


    /**
     * Returns the declared mixin node types of this node.
     * <p/>
     * The default implementation uses the values of the
     * <code>jcr:mixinTypes</code> property to look up the mixin node types
     * from the {@link NodeTypeManager} of the current workspace.
     *
     * @return mixin node types
     * @throws RepositoryException if an error occurs
     */
    @Override
    public NodeType[] getMixinNodeTypes() throws RepositoryException {
        NodeTypeManager manager = getSession().getWorkspace().getNodeTypeManager();
        Property property = mixinProperty();
        Value[] values = property.getValues();
        if (values == null) return new NodeType[0];
        NodeType[] types = new NodeType[values.length];
        for (int i = 0; i < values.length; i++) {
            types[i] = manager.getNodeType(values[i].getString());
        }
        return types;
    }


    private String getJcrNameForQName(String name) throws RepositoryException {
        return getJcrName(NameFactoryImpl.getInstance().create(name));
    }


    private String getJcrName(Name name) throws RepositoryException {
        return session.getNamePathResolver().getJCRName(name);
    }


    @Override
    public boolean isNodeType(String nodeTypeName) throws RepositoryException {
        return primaryTypeProperty().getString().equals(nodeTypeName);
    }


    @Override
    public void setPrimaryType(String nodeTypeName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
        setProperty(getJcrNameForQName(Property.JCR_PRIMARY_TYPE), nodeTypeName);
    }


    /**
     * Very simple implementation of Mixin support: Does not check permissions, check for conflicts, etc.
     */
    @Override
    public void addMixin(String mixinName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
        Property mixinProperty = mixinProperty();
        Value[] values = mixinProperty.getValues();
        final Value[] newValues;
        if (values == null) {
            newValues = new Value[1];
            newValues[0] = new ValueImpl(mixinName);
        }
        else {
            newValues = new Value[values.length + 1];
            System.arraycopy(values, 0, newValues, 0, values.length);
            newValues[values.length] = new ValueImpl(mixinName);
        }
        mixinProperty.setValue(newValues);
        session.changeItem(this);
    }


    @Override
    public void removeMixin(String mixinName) throws NoSuchNodeTypeException, VersionException, ConstraintViolationException, LockException, RepositoryException {
        Property mixinProperty = mixinProperty();
        Value[] values = mixinProperty.getValues();
        if (values == null) {
            throw new NoSuchNodeTypeException(mixinName);
        }

        boolean found = false;
        Value[] newValues = new Value[values.length - 1];
        for (int idx = 0, newIdx = 0; idx < values.length; ) {
            Value value = values[idx];
            if (value.getString().equals(mixinName)) {
                idx++;
                found = true;
            }
            else {
                if (newIdx < newValues.length)
                    newValues[newIdx] = values[idx];
                idx++;
                newIdx++;
            }
        }

        if (found) {
            mixinProperty.setValue(newValues);
            session.changeItem(this);
        }
    }


    @Override
    public boolean canAddMixin(String mixinName) throws NoSuchNodeTypeException, RepositoryException {
        return true;
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
        return null;  //deprecated
    }


    @Override
    public String getCorrespondingNodePath(String workspaceName) throws ItemNotFoundException, NoSuchWorkspaceException, AccessDeniedException, RepositoryException {
        return null;  //Not Implemented
    }


    @Override
    public NodeIterator getSharedSet() throws RepositoryException {
        return new NodeIteratorImpl(singletonList((Node)this));
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

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

import com.google.common.base.Strings;
import com.twcable.jackalope.impl.common.Paths;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
import java.util.Objects;

/**
 * In memory JCR Item
 */
@SuppressWarnings("DuplicateThrows")
public abstract class ItemImpl implements Item {
    protected final SessionImpl session;
    private String path;


    /**
     * Construct an implementation of the jcr Item interface.
     *
     * @param session The session for which this Item is being constructed
     * @param path    The jcr path of this item
     * @throws ItemNotFoundException
     * @throws ItemExistsException
     */
    ItemImpl(@Nonnull SessionImpl session, @Nonnull String path) throws ItemNotFoundException, ItemExistsException {
        this.session = session;
        this.path = path;
        session.addItem(this);
    }


    @Override
    @Nonnull
    public String getPath() {
        return path;
    }


    void setPath(String path) {
        this.path = path;
    }


    @Override
    @Nonnull
    public String getName() {
        return Paths.basename(path);
    }


    @Override
    @Nullable
    public Item getAncestor(int depth) throws ItemNotFoundException, RepositoryException {
        int myDepth = getDepth();
        if (depth > myDepth) throw new ItemNotFoundException();
        return (depth < myDepth) ? ((NodeImpl)getParent()).getAncestor(depth) : null;
    }


    @Override
    @Nonnull
    public Node getParent() throws ItemNotFoundException, RepositoryException {
        if (session.getRootNode() == this) throw new ItemNotFoundException();
        if (Strings.isNullOrEmpty(Paths.parent(path))) return session.getRootNode();
        return session.getNode(Paths.parent(path));
    }


    ItemImpl getParentImpl() {
        try {
            return (ItemImpl)getParent();
        }
        catch (RepositoryException re) {
            return null;
        }
    }


    @Override
    public int getDepth() {
        return Paths.depth(path);
    }


    @Override
    public Session getSession() {
        return session;
    }


    @Override
    abstract public boolean isNode();


    @Override
    public boolean isNew() {
        return session.isNew(this);
    }


    @Override
    public boolean isModified() {
        return session.isModified(this);
    }


    @Override
    public boolean isSame(Item otherItem) throws RepositoryException {
        return Objects.equals(path, otherItem.getPath())
            && (isNode() == otherItem.isNode())
            && (isNode() || getParent().isSame(otherItem.getParent()));
    }


    @Override
    public abstract void accept(ItemVisitor visitor) throws RepositoryException;


    @Override
    public void save() {
        session.save(this);
    }


    @Override
    public void refresh(boolean keepChanges) throws InvalidItemStateException, RepositoryException {
        //Not implemented
    }


    @Override
    public void remove() throws VersionException, LockException, ConstraintViolationException, AccessDeniedException, RepositoryException {
        session.removeItem(this);
    }
}

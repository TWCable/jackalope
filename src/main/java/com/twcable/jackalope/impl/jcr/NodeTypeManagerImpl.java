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

import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.nodetype.InvalidNodeTypeDefinitionException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeDefinitionTemplate;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeDefinition;
import javax.jcr.nodetype.NodeTypeExistsException;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple implementation of {@link NodeTypeManager}. Use {@link #registerNodeType(NodeType)} to add NodeTypes.
 */
@SuppressWarnings("DuplicateThrows")
public class NodeTypeManagerImpl implements NodeTypeManager {
    private Map<String, NodeType> nameToNodeType = new LinkedHashMap<>();


    @Override
    public NodeType getNodeType(String nodeTypeName) throws NoSuchNodeTypeException, RepositoryException {
        NodeType nodeType = nameToNodeType.get(nodeTypeName);
        if (nodeType == null) throw new NoSuchNodeTypeException(nodeTypeName);
        return nodeType;
    }


    @Override
    public boolean hasNodeType(String name) throws RepositoryException {
        return nameToNodeType.containsKey(name);
    }


    @Override
    public NodeTypeIterator getAllNodeTypes() throws RepositoryException {
        return new NodeTypeIteratorImpl(nameToNodeType.values().iterator());
    }


    @Override
    public NodeTypeIterator getPrimaryNodeTypes() throws RepositoryException {
        ArrayList<NodeType> nodeTypes = new ArrayList<>();
        for (NodeType nodeType : nameToNodeType.values()) {
            if (!nodeType.isMixin()) nodeTypes.add(nodeType);
        }
        return new NodeTypeIteratorImpl(nodeTypes.iterator());
    }


    @Override
    public NodeTypeIterator getMixinNodeTypes() throws RepositoryException {
        ArrayList<NodeType> nodeTypes = new ArrayList<>();
        for (NodeType nodeType : nameToNodeType.values()) {
            if (nodeType.isMixin()) nodeTypes.add(nodeType);
        }
        return new NodeTypeIteratorImpl(nodeTypes.iterator());
    }


    @Override
    public NodeTypeTemplate createNodeTypeTemplate() throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedOperationException();
    }


    @Override
    public NodeTypeTemplate createNodeTypeTemplate(NodeTypeDefinition ntd) throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedOperationException();
    }


    @Override
    public NodeDefinitionTemplate createNodeDefinitionTemplate() throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedOperationException();
    }


    @Override
    public PropertyDefinitionTemplate createPropertyDefinitionTemplate() throws UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedOperationException();
    }


    @Override
    public NodeType registerNodeType(NodeTypeDefinition ntd, boolean allowUpdate) throws InvalidNodeTypeDefinitionException, NodeTypeExistsException, UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedOperationException();
    }


    @SuppressWarnings("unused")
    public void registerNodeType(NodeType nt) {
        nameToNodeType.put(nt.getName(), nt);
    }


    @Override
    public NodeTypeIterator registerNodeTypes(NodeTypeDefinition[] ntds, boolean allowUpdate) throws InvalidNodeTypeDefinitionException, NodeTypeExistsException, UnsupportedRepositoryOperationException, RepositoryException {
        throw new UnsupportedOperationException();
    }


    @Override
    public void unregisterNodeType(String name) throws UnsupportedRepositoryOperationException, NoSuchNodeTypeException, RepositoryException {
        nameToNodeType.remove(name);
    }


    @Override
    public void unregisterNodeTypes(String[] names) throws UnsupportedRepositoryOperationException, NoSuchNodeTypeException, RepositoryException {
        for (String name : names) {
            unregisterNodeType(name);
        }
    }


    private static class NodeTypeIteratorImpl implements NodeTypeIterator {
        private final Iterator<NodeType> iterator;


        public NodeTypeIteratorImpl(Iterator<NodeType> iterator) {
            this.iterator = iterator;
        }


        @Override
        public NodeType nextNodeType() {
            return (NodeType)next();
        }


        @Override
        public void skip(long skipNum) {
            throw new UnsupportedOperationException();
        }


        @Override
        public long getSize() {
            throw new UnsupportedOperationException();
        }


        @Override
        public long getPosition() {
            throw new UnsupportedOperationException();
        }


        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }


        @Override
        public Object next() {
            return iterator.next();
        }


        @Override
        public void remove() {
            iterator.remove();
        }
    }
}

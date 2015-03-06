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

package com.twcable.jackalope.impl.sling;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.twcable.jackalope.JcrConstants;
import com.twcable.jackalope.impl.common.Paths;
import com.twcable.jackalope.impl.cq.AssetImpl;
import com.twcable.jackalope.impl.cq.PageImpl;
import com.twcable.jackalope.impl.jcr.JcrUtils;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.JcrModifiablePropertyMap;
import org.apache.sling.jcr.resource.JcrPropertyMap;
import org.apache.sling.jcr.resource.internal.JcrModifiableValueMap;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;
import static org.apache.jackrabbit.JcrConstants.JCR_DATA;
import static org.apache.jackrabbit.JcrConstants.JCR_PRIMARYTYPE;
import static org.apache.jackrabbit.JcrConstants.NT_FILE;

/**
 * Implement sling Resource for a jcr Node
 */
public class NodeResourceImpl extends ItemResourceImpl {
    private final Node node;


    /**
     * Construct a new NodeResourceImpl.
     *
     * @param resourceResolver ResourceResolver that constructed this NodeResourceImpl
     * @param node             The constructed ResourceNodeImpl
     */
    public NodeResourceImpl(ResourceResolver resourceResolver, Node node) {
        super(resourceResolver, node);
        this.node = node;
    }


    @Override
    public Iterator<Resource> listChildren() {
        return getChildren().iterator();
    }


    @Override
    public Iterable<Resource> getChildren() {
        try {
            List<Resource> children = new ArrayList<>();
            for (Node child : JcrUtils.getChildNodes(node))
                children.add(resourceResolver.getResource(this, child.getName()));
            return children;
        }
        catch (RepositoryException re) {
            return new ArrayList<>();
        }
    }


    @Override
    public Resource getChild(String relPath) {
        try {
            return resourceResolver.getResource(Paths.resolve(node.getPath(), relPath));
        }
        catch (RepositoryException re) {
            throw new SlingRepositoryException(re);
        }
    }


    @Override
    public String getResourceType() {
        try {
            return getResourceTypeForNode(node);
        }
        catch (RepositoryException re) {
            return null; /* ignore */
        }
    }


    @Override
    public String getResourceSuperType() {
        return null; //TODO: implement resource supertypes
    }


    @Override
    @SuppressWarnings({"unchecked", "deprecation"})
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        if (type.equals(Node.class)) return (AdapterType)node;
        if (type.equals(ValueMap.class) || type.equals(Map.class)) return (AdapterType)new JcrPropertyMap(node);
        if (type.equals(PersistableValueMap.class)) return (AdapterType)new JcrModifiablePropertyMap(node);
        if (type.equals(ModifiableValueMap.class)) return (AdapterType)new JcrModifiableValueMap(node, null);

        if (type.equals(Page.class)) {
            try {
                ValueMap properties = this.adaptTo(ValueMap.class);
                if (properties == null) return null;
                String primaryType = properties.get(JCR_PRIMARYTYPE, String.class);
                if (primaryType == null || !primaryType.equals(JcrConstants.CQ_PAGE)) return null;
                if (!node.hasNode(JCR_CONTENT)) return null;
                return (AdapterType)new PageImpl(new NodeResourceImpl(resourceResolver, node));
            }
            catch (RepositoryException e) {
                return null;
            }
        }

        if (type.equals(Asset.class)) {
            try {
                ValueMap properties = ResourceUtil.getValueMap(this);
                return !"dam:Asset".equals(properties.get(JCR_PRIMARYTYPE, String.class)) ? null :
                    !node.hasNode(JCR_CONTENT) ? null :
                        (AdapterType)new AssetImpl(this);
            }
            catch (RepositoryException e) {
                return null;
            }
        }

        if (type.equals(InputStream.class)) {
            try {
                Node content = node.isNodeType(NT_FILE) ? node.getNode(JCR_CONTENT) : node;
                Property data = content.hasProperty(JCR_DATA) ? content.getProperty(JCR_DATA) : null;
                return data != null ? (AdapterType)data.getBinary().getStream() : null;

            }
            catch (RepositoryException e) {
                return null;
            }
        }

        return super.adaptTo(type);
    }

}

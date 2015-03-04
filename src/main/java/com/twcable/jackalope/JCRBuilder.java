package com.twcable.jackalope;

import com.google.common.base.Strings;
import com.twcable.jackalope.impl.common.Values;
import com.twcable.jackalope.impl.jcr.NodeImpl;
import com.twcable.jackalope.impl.jcr.SessionImpl;
import com.twcable.jackalope.impl.sling.NodeResourceImpl;
import com.twcable.jackalope.impl.sling.ResourceResolverImpl;
import com.twcable.jackalope.impl.sling.SimpleResourceResolverFactory;
import com.twcable.jackalope.impl.sling.SlingRepositoryException;
import com.twcable.jackalope.impl.sling.SlingRepositoryImpl;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.jcr.api.SlingRepository;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import java.io.File;

/**
 * Utilities for building an in-memory jcr implementation for use in unit tests.
 */
public class JCRBuilder {

    private JCRBuilder() {
    }


    @Nonnull
    public static RepositoryBuilder repository(NodeBuilder... nodeBuilders) {
        return new RepositoryBuilderImpl(nodeBuilders);
    }


    /**
     * Creates a ResourceBuilder with the given node.
     * <p/>
     * The ResourceBuilder will create a resource from the node built by nodeBuilder.
     *
     * @param nodeBuilder The node builder that will build the node that the resource will use
     * @return the ResourceBuilder
     */
    @Nonnull
    public static ResourceBuilder resource(NodeBuilder nodeBuilder) {
        return new ResourceBuilderImpl(nodeBuilder);
    }


    /**
     * Creates a NodeBuilder with the given contents.
     * <p/>
     * The NodeBuilder will build an nt:unstructured node.  Its name is derived from the path by default. If you pass
     * in a NodeBuilder, the Node it creates will be added as a child of the Node. If you pass in a PropertyBuilderImpl,
     * it will set a property of the Node.
     * <p/>
     *
     * @param path   the path of the node
     * @param values builders for the Node's child items (properties, child nodes)
     * @return the NodeBuilder
     */
    @Nonnull
    public static NodeBuilder node(String path, ItemBuilder... values) {
        return node(path, JcrConstants.NT_UNSTRUCTURED, values);
    }


    /**
     * Creates a NodeBuilder with the given contents.
     * <p/>
     * The NodeBuilder will build a node of the specified type.  Its name is derived from the path by default. If you pass
     * in a NodeBuilder, the Node it creates will be added as a child of the Node. If you pass in a PropertyBuilderImpl,
     * it will set a property of the Node.
     *
     * @param path         the path of the node
     * @param nodeTypeName the node type to use for this node
     * @param values       builders for the Node's child items (properties, child nodes)
     * @return the NodeBuilder
     */
    @Nonnull
    public static NodeBuilder node(String path, String nodeTypeName, ItemBuilder... values) {
        return new NodeBuilderImpl(path, nodeTypeName, values);
    }


    /**
     * Creates a PropertyBuilderImpl with the given name and value.
     * <p/>
     *
     * @param name  the name of the property
     * @param value the value of the property
     * @return the PropertyBuilderImpl
     */
    @Nonnull
    public static PropertyBuilder property(String name, Object value) {
        return new PropertyBuilderImpl(name, value);
    }


    /**
     * Creates a PropertyBuilderImpl that will build a multi-value property with the given name and values.
     * <p/>
     *
     * @param name   the name of the property
     * @param values the values of the property
     * @return the PropertyBuilderImpl
     */
    @Nonnull
    public static PropertyBuilder property(String name, Object[] values) {
        return new PropertyBuilderImpl(name, values);
    }


    // **********************************************************************
    //
    // INNER CLASSES
    //
    // **********************************************************************


    static class RepositoryBuilderImpl implements RepositoryBuilder {
        NodeBuilder[] nodeBuilders;


        RepositoryBuilderImpl(NodeBuilder... nodeBuilders) {
            this.nodeBuilders = nodeBuilders;
        }


        public SlingRepository build() {
            SlingRepositoryImpl repository = new SlingRepositoryImpl();
            try {
                for (NodeBuilder nodeBuilder : nodeBuilders)
                    ((NodeBuilderImpl)nodeBuilder).build(repository.login().getRootNode());
            }
            catch (RepositoryException re) {
                throw new SlingRepositoryException(re);
            }
            return repository;
        }
    }

    static class NodeBuilderImpl implements NodeBuilder {
        private final String path;
        private final String nodeTypeName;
        private final ItemBuilder[] childBuilders;


        NodeBuilderImpl(String path, String nodeTypeName, ItemBuilder... childBuilders) {
            this.path = path;
            this.nodeTypeName = nodeTypeName;
            this.childBuilders = childBuilders;
        }


        /**
         * Builds the requested node.
         *
         * @return the Node
         */
        public Node build() {
            return build((NodeImpl)null);
        }


        /**
         * Builds the requested node.
         *
         * @param parent The parent node of the node to be built.  If the node does not need a parent, null can be used.
         * @return the Node
         */
        public NodeImpl build(Node parent) {
            try {
                NodeImpl node = (parent != null) ? (NodeImpl)parent.addNode(getName(), nodeTypeName) : new NodeImpl(new SessionImpl(), getName());
                if (!Strings.isNullOrEmpty(nodeTypeName))
                    node.setPrimaryType(nodeTypeName);
                for (ItemBuilder builder : childBuilders)
                    builder.build(node);
                node.getSession().save();
                return node;
            }
            catch (RepositoryException re) {
                throw new SlingRepositoryException(re);
            }
        }


        /**
         * Builds the requested node.
         *
         * @param session Session to be associated with the new node
         * @return the Node
         */
        protected NodeImpl build(Session session) {
            try {
                NodeImpl node = new NodeImpl((SessionImpl)session, getName());
                if (!Strings.isNullOrEmpty(nodeTypeName))
                    node.setPrimaryType(nodeTypeName);
                for (ItemBuilder builder : childBuilders)
                    builder.build(node);
                node.getSession().save();
                return node;
            }
            catch (RepositoryException re) {
                throw new SlingRepositoryException(re);
            }
        }


        private String getName() {
            return new File(path).getName();
        }
    }

    static class ResourceBuilderImpl implements ResourceBuilder {
        private final NodeBuilder builder;


        ResourceBuilderImpl(NodeBuilder builder) {
            this.builder = builder;
        }


        /**
         * Builds the requested resource.
         *
         * @return the Resource
         */
        public Resource build() {
            try {
                ResourceResolverImpl resolver = (ResourceResolverImpl)new SimpleResourceResolverFactory(new SlingRepositoryImpl()).getAdministrativeResourceResolver(null);
                return new NodeResourceImpl(resolver, ((NodeBuilderImpl)builder).build(resolver.adaptTo(Session.class)));
            }
            catch (LoginException le) {
                throw new SlingRepositoryException(le);
            }
        }
    }

    static class PropertyBuilderImpl implements PropertyBuilder {
        private final String name;
        private final Value[] values;
        private final boolean hasMultiple;


        PropertyBuilderImpl(String name, Object value) {
            this.name = name;
            this.values = Values.convertObjectsToValues(value);
            this.hasMultiple = false;
        }


        PropertyBuilderImpl(String name, Object[] values) {
            this.name = name;
            this.values = Values.convertObjectsToValues(values);
            this.hasMultiple = true;
        }


        public Property build(Node parent) {
            if (parent == null) return null; // properties only exist in nodes so there is nothing to build.
            try {
                if (hasMultiple)
                    parent.setProperty(name, values);
                else
                    parent.setProperty(name, values[0]);
                return parent.getProperty(name);
            }
            catch (RepositoryException re) {
                throw new SlingRepositoryException(re);
            }
        }
    }

}


package com.twcable.jackalope.impl.jcr;

import javax.jcr.nodetype.NodeDefinition;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.OnParentVersionAction;

/**
 * Simple implementation of an {@link NodeDefinition} hardcoded to "nt:base"
 */
public class NodeDefinitionImpl implements NodeDefinition {

    @Override
    public NodeType[] getRequiredPrimaryTypes() {
        return new NodeType[]{new NodeTypeImpl("nt:base")};
    }


    @Override
    public String[] getRequiredPrimaryTypeNames() {
        return new String[]{"nt:base"};
    }


    @Override
    public NodeType getDefaultPrimaryType() {
        return new NodeTypeImpl("nt:base");
    }


    @Override
    public String getDefaultPrimaryTypeName() {
        return "nt:base";
    }


    @Override
    public boolean allowsSameNameSiblings() {
        return false;
    }


    @Override
    public NodeType getDeclaringNodeType() {
        return null;
    }


    @Override
    public String getName() {
        return "nt:base";
    }


    @Override
    public boolean isAutoCreated() {
        return false;
    }


    @Override
    public boolean isMandatory() {
        return false;
    }


    @Override
    public int getOnParentVersion() {
        return OnParentVersionAction.IGNORE;
    }


    @Override
    public boolean isProtected() {
        return false;
    }

}

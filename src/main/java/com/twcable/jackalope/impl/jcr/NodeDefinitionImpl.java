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

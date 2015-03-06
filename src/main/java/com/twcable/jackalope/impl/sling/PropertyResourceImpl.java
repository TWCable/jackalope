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

import com.twcable.jackalope.impl.common.Values;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.math.BigDecimal;
import java.util.Calendar;

public class PropertyResourceImpl extends ItemResourceImpl {
    private final Property property;


    public PropertyResourceImpl(ResourceResolver resourceResolver, Property property) {
        super(resourceResolver, property);
        this.property = property;
    }


    @Override
    public Iterable<Resource> getChildren() {
        return null;
    }


    @Override
    public String getResourceType() {
        // Taken from the sling implementation
        try {
            return getResourceTypeForNode(property.getParent()) + "/" + property.getName();
        }
        catch (RepositoryException re) {
            throw new SlingRepositoryException(re);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        try {
            return (type == String.class) ? (AdapterType)property.getString() :
                (type == Boolean.class) ? (AdapterType)Boolean.valueOf(property.getBoolean()) :
                    (type == Long.class) ? (AdapterType)Long.valueOf(property.getLong()) :
                        (type == Double.class) ? (AdapterType)new Double(property.getDouble()) :
                            (type == BigDecimal.class) ? (AdapterType)property.getDecimal() :
                                (type == Calendar.class) ? (AdapterType)property.getDate() :
                                    (type == Value.class) ? (AdapterType)property.getValue() :
                                        (type == String[].class) ? (AdapterType)Values.convertValuesToStrings(property.getValues()) :
                                            (type == Value[].class) ? (AdapterType)property.getValues() :
                                                super.adaptTo(type);
        }
        catch (RepositoryException re) {
            return super.adaptTo(type);
        }
    }
}

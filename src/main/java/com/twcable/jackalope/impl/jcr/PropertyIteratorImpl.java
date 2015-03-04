package com.twcable.jackalope.impl.jcr;

import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import java.util.Collection;

class PropertyIteratorImpl extends RangeIteratorImpl<Property> implements PropertyIterator {
    PropertyIteratorImpl(Collection<Property> properties) {
        super(properties);
    }


    @Override
    public Property nextProperty() {
        return (Property)next();
    }
}

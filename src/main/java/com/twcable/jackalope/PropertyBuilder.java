package com.twcable.jackalope;

import javax.jcr.Node;
import javax.jcr.Property;

public interface PropertyBuilder extends ItemBuilder<Property> {
    public Property build(Node parent);
}

package com.twcable.jackalope;

import javax.jcr.Node;

public interface ItemBuilder<T> extends Builder {
    public T build(Node parent);
}

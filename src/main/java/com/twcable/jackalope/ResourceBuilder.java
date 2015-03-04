package com.twcable.jackalope;

import org.apache.sling.api.resource.Resource;

public interface ResourceBuilder extends Builder {
    public Resource build();
}

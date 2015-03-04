package com.twcable.jackalope;

import org.apache.sling.jcr.api.SlingRepository;

public interface RepositoryBuilder {
    public SlingRepository build();
}

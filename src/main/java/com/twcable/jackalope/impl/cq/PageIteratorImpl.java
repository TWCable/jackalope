/*
 * Copyright 2014-2016 Time Warner Cable, Inc.
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
package com.twcable.jackalope.impl.cq;

import com.day.cq.commons.Filter;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;
import org.apache.sling.api.resource.Resource;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple iterator over filtered Pages under a Resource
 */
public class PageIteratorImpl implements Iterator<Page> {

    private final Iterator<Page> iterator;


    public PageIteratorImpl(Resource resource, Filter<Page> filter, boolean deep) {
        this.iterator = getChildren(resource, filter, deep).iterator();
    }

    public PageIteratorImpl(Resource resource, Filter<Page> filter) {
        this(resource, filter, false);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }


    @Override
    public Page next() {
        return iterator.next();
    }


    @Override
    public void remove() {
        iterator.remove();
    }

    private List<Page> getChildren(Resource resource, Filter<Page> filter, boolean deep) {
        List<Page> pages = new LinkedList<>();
        for (Resource next : Lists.newArrayList(resource.listChildren())) {
            Page page = next.adaptTo(Page.class);
            if (page == null || !filter.includes(page)) continue;
            pages.add(page);
            if (deep) {
                pages.addAll(getChildren(page.adaptTo(Resource.class), filter, true));
            }
        }
        return pages;
    }
}

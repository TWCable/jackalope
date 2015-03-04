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


    public PageIteratorImpl(Resource resource, Filter<Page> filter) {
        List<Page> pages = new LinkedList<>();
        for (Resource next : Lists.newArrayList(resource.listChildren())) {
            Page page = next.adaptTo(Page.class);
            if (page == null || !filter.includes(page)) continue;
            pages.add(page);
        }

        this.iterator = pages.iterator();
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
}

package com.twcable.jackalope.impl.jcr;

import javax.jcr.RangeIterator;
import java.util.Collection;
import java.util.Iterator;

/**
 * A simple implementation of RangeIterator that delegates to a single simple Iterator.
 *
 * @param <T> Type of elements in the Collection
 */
class RangeIteratorImpl<T> implements RangeIterator {
    final int size;
    final Iterator<T> iterator;
    int position = 0;


    /**
     * Construct a RangeIteratorImpl.
     *
     * @param source The collection to iterate over.
     */
    public RangeIteratorImpl(Collection<T> source) {
        this.size = source.size();
        this.iterator = source.iterator();
    }


    @Override
    public void skip(long skipNum) {
        while (skipNum-- > 0) next();
    }


    @Override
    public long getSize() {
        return size;
    }


    @Override
    public long getPosition() {
        return position;
    }


    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }


    @Override
    public Object next() {
        position++;
        return iterator.next();
    }


    @Override
    public void remove() {
        iterator.remove();
    }
}

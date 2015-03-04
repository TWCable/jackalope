package com.twcable.jackalope.impl.common;

import com.twcable.jackalope.impl.jcr.ValueImpl;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for manipulating {@link Value}s.
 */
public final class Values {

    private Values() {
    }


    public static String[] convertValuesToStrings(Value... values) throws RepositoryException {
        List<String> strings = new ArrayList<>();
        for (Value value : values)
            strings.add(value.getString());
        return strings.toArray(new String[strings.size()]);
    }


    public static Value[] convertStringsToValues(String... strings) {
        List<Value> values = new ArrayList<>();
        for (String string : strings)
            values.add(new ValueImpl(string));
        return values.toArray(new Value[values.size()]);
    }


    public static Value[] convertObjectsToValues(Object... strings) {
        List<Value> values = new ArrayList<>();
        for (Object object : strings)
            values.add(new ValueImpl(object));
        return values.toArray(new Value[values.size()]);
    }
}

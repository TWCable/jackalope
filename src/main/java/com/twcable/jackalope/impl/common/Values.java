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

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
package com.twcable.jackalope.impl.jcr;

import javax.jcr.Binary;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Objects;

/**
 * In memory dummy value for testing
 */
// TODO: Implement the various conversions
// TODO: Implement proper calendar getString
@SuppressWarnings("DuplicateThrows")
public class ValueImpl implements Value {
    private final int type;
    private final Object valueObject;


    /**
     * Construct an implementation of the jcr Value interface
     *
     * @param value The value to be stored
     * @throws IllegalArgumentException If the type of Value can't be converted to a valid jcr value type.
     */
    public ValueImpl(Object value) throws IllegalArgumentException {
        this(selectPropertyType(value), value);
    }


    private static int selectPropertyType(Object value) {
        return (value instanceof String) ? PropertyType.STRING :
            (value instanceof Long) ? PropertyType.LONG :
                (value instanceof Double) ? PropertyType.DOUBLE :
                    (value instanceof BigDecimal) ? PropertyType.DECIMAL :
                        (value instanceof Calendar) ? PropertyType.DATE :
                            (value instanceof Boolean) ? PropertyType.BOOLEAN :
                                (value instanceof Binary) ? PropertyType.BINARY :
                                    PropertyType.UNDEFINED;
    }


    /**
     * Construct an implementation of the jcr Value interface
     *
     * @param type  The type of value to be stored
     * @param value The value to be stored
     * @throws IllegalArgumentException If the type of Value can't be converted to a valid jcr value type.
     */
    public ValueImpl(int type, Object value) throws IllegalArgumentException {
        this.type = type;
        this.valueObject = value;
    }


    @Override
    public String getString() throws ValueFormatException, IllegalStateException, RepositoryException {
        return valueObject.toString();
    }


    @Override
    public InputStream getStream() throws RepositoryException {
        return null;  // Deprecated
    }


    @Override
    public Binary getBinary() throws RepositoryException {
        if (type != PropertyType.BINARY) throw new ValueFormatException();
        return (Binary)valueObject;
    }


    @Override
    public long getLong() throws ValueFormatException, RepositoryException {
        if (type != PropertyType.LONG) throw new ValueFormatException();
        return (long)valueObject;
    }


    @Override
    public double getDouble() throws ValueFormatException, RepositoryException {
        if (type != PropertyType.DOUBLE) throw new ValueFormatException();
        return (double)valueObject;
    }


    @Override
    public BigDecimal getDecimal() throws ValueFormatException, RepositoryException {
        if (type != PropertyType.DECIMAL) throw new ValueFormatException();
        return (BigDecimal)valueObject;
    }


    @Override
    public Calendar getDate() throws ValueFormatException, RepositoryException {
        if (type != PropertyType.DATE) throw new ValueFormatException();
        return (Calendar)valueObject;
    }


    @Override
    public boolean getBoolean() throws ValueFormatException, RepositoryException {
        if (type != PropertyType.BOOLEAN) throw new ValueFormatException();
        return (boolean)valueObject;
    }


    @Override
    public int getType() {
        return type;
    }


    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValueImpl valueImpl = (ValueImpl)o;

        if (type != valueImpl.type) return false;

        return Objects.equals(valueObject, valueImpl.valueObject);
    }


    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + (valueObject != null ? valueObject.hashCode() : 0);
        return result;
    }
}

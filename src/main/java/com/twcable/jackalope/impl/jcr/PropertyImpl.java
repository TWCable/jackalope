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

import com.google.common.primitives.Longs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.Binary;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.VersionException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Implementation of jcr Property Interface.
 */
@SuppressWarnings("DuplicateThrows")
public class PropertyImpl extends ItemImpl implements Property {
    private Value value;
    private Value[] values;


    public PropertyImpl(@Nonnull SessionImpl session, @Nonnull String path) throws ItemNotFoundException, ItemExistsException {
        super(session, path);
    }


    public PropertyImpl(@Nonnull SessionImpl session, @Nonnull String path, @Nonnull Value value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        this(session, path);
        setValue(value);
    }


    public PropertyImpl(@Nonnull SessionImpl session, @Nonnull String path, @Nonnull Value[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        this(session, path);
        setValue(values);
    }


    public PropertyImpl(@Nonnull SessionImpl session, @Nonnull String path, @Nonnull String[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        this(session, path);
        List<Value> stringValues = new ArrayList<>(values.length);
        for (String value : values)
            stringValues.add(new ValueImpl(value));
        setValue(stringValues.toArray(new Value[stringValues.size()]));
    }


    @Override
    public void setValue(@Nonnull Value value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        this.value = value;
        session.changeItem(this);
    }


    @Override
    public void setValue(@Nonnull Value[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        this.values = values;
        session.changeItem(this);
    }


    @Override
    public void setValue(@Nonnull String value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        setValue(new ValueImpl(value));
    }


    @Override
    public void setValue(@Nonnull String[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        List<Value> valueList = new ArrayList<>(values.length);
        for (String value : values)
            valueList.add(new ValueImpl(value));
        setValue(valueList.toArray(new Value[valueList.size()]));
    }


    @Override
    public void setValue(InputStream value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        //Deprecated
    }


    @Override
    public void setValue(@Nonnull Binary value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        setValue(new ValueImpl(value));
    }


    @Override
    public void setValue(long value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        setValue(new ValueImpl(value));
    }


    @Override
    public void setValue(double value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        setValue(new ValueImpl(value));
    }


    @Override
    public void setValue(@Nonnull BigDecimal value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        setValue(new ValueImpl(value));
    }


    @Override
    public void setValue(@Nonnull Calendar value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        setValue(new ValueImpl(value));
    }


    @Override
    public void setValue(boolean value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        setValue(new ValueImpl(value));
    }


    @Override
    public void setValue(@Nonnull Node value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        //Not implemented
    }


    @Override
    @Nonnull
    public Value getValue() throws ValueFormatException, RepositoryException {
        return value;
    }


    @Override
    @Nonnull
    public Value[] getValues() throws ValueFormatException, RepositoryException {
        return values;
    }


    @Override
    @Nonnull
    public String getString() throws ValueFormatException, RepositoryException {
        if (isMultiple()) throw new ValueFormatException();
        return value.getString();  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    @Nullable
    public InputStream getStream() throws ValueFormatException, RepositoryException {
        return null;  // deprecated
    }


    @Override
    @Nonnull
    public Binary getBinary() throws ValueFormatException, RepositoryException {
        if (isMultiple()) throw new ValueFormatException();
        return value.getBinary();  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public long getLong() throws ValueFormatException, RepositoryException {
        if (isMultiple()) throw new ValueFormatException();
        return value.getLong();  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public double getDouble() throws ValueFormatException, RepositoryException {
        if (isMultiple()) throw new ValueFormatException();
        return value.getDouble();  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    @Nonnull
    public BigDecimal getDecimal() throws ValueFormatException, RepositoryException {
        if (isMultiple()) throw new ValueFormatException();
        return value.getDecimal();  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    @Nonnull
    public Calendar getDate() throws ValueFormatException, RepositoryException {
        if (isMultiple()) throw new ValueFormatException();
        return value.getDate();  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public boolean getBoolean() throws ValueFormatException, RepositoryException {
        if (isMultiple()) throw new ValueFormatException();
        return value.getBoolean();  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Node getNode() throws ItemNotFoundException, ValueFormatException, RepositoryException {
        return null;  //not implemented
    }


    @Override
    public Property getProperty() throws ItemNotFoundException, ValueFormatException, RepositoryException {
        return null;  //not implemented
    }


    @Override
    public long getLength() throws ValueFormatException, RepositoryException {
        if (isMultiple()) throw new ValueFormatException();
        return getString().length();
    }


    @Override
    @Nonnull
    public long[] getLengths() throws ValueFormatException, RepositoryException {
        if (!isMultiple()) throw new ValueFormatException();
        List<Long> lengths = new ArrayList<>();
        for (Value value : values)
            lengths.add((long)value.getString().length());
        return Longs.toArray(lengths);
    }


    @Override
    public PropertyDefinition getDefinition() throws RepositoryException {
        return new PropertyDefinition() {
            @Override
            public int getRequiredType() {
                return 0;
            }


            @Override
            public String[] getValueConstraints() {
                return new String[0];
            }


            @Override
            public Value[] getDefaultValues() {
                return new Value[0];
            }


            @Override
            public boolean isMultiple() {
                return PropertyImpl.this.isMultiple();
            }


            @Override
            public String[] getAvailableQueryOperators() {
                return new String[0];
            }


            @Override
            public boolean isFullTextSearchable() {
                return false;
            }


            @Override
            public boolean isQueryOrderable() {
                return false;
            }


            @Override
            public NodeType getDeclaringNodeType() {
                return null;
            }


            @Override
            public String getName() {
                return null;
            }


            @Override
            public boolean isAutoCreated() {
                return false;
            }


            @Override
            public boolean isMandatory() {
                return false;
            }


            @Override
            public int getOnParentVersion() {
                return 0;
            }


            @Override
            public boolean isProtected() {
                return false;
            }
        };
    }


    @Override
    public int getType() throws RepositoryException {
        return !isMultiple() ? value.getType() :
            values.length > 0 ? values[0].getType() :
                PropertyType.UNDEFINED;
    }


    @Override
    public boolean isMultiple() {
        return values != null;
    }


    @Override
    public boolean isNode() {
        return false;
    }


    @Override
    public void accept(ItemVisitor visitor) throws RepositoryException {
        visitor.visit(this);
    }
}

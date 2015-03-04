package com.twcable.jackalope.impl.jcr;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Simple implementation of an {@link ValueFactory}
 */
public class ValueFactoryImpl implements ValueFactory {

    @Override
    public Value createValue(String value) {
        return new ValueImpl(PropertyType.STRING, value);
    }


    @Override
    public Value createValue(String value, int type) throws ValueFormatException {
        switch (type) {
            case PropertyType.STRING:
                return createValue(value);
            case PropertyType.LONG:
                return createValue(Long.valueOf(value));
            case PropertyType.DOUBLE:
                return createValue(Double.valueOf(value));
            case PropertyType.BOOLEAN:
                return createValue(Boolean.valueOf(value));
            case PropertyType.DECIMAL:
                return createValue(new BigDecimal(value));
            case PropertyType.DATE: // TODO: parse dates
            case PropertyType.BINARY:
                return createValue(createBinary(value));
            default:
                return null;
        }
    }


    @Override
    public Value createValue(long value) {
        return new ValueImpl(PropertyType.LONG, value);
    }


    @Override
    public Value createValue(double value) {
        return new ValueImpl(PropertyType.DOUBLE, value);
    }


    @Override
    public Value createValue(BigDecimal value) {
        return new ValueImpl(PropertyType.DECIMAL, value);
    }


    @Override
    public Value createValue(boolean value) {
        return new ValueImpl(PropertyType.BOOLEAN, value);
    }


    @Override
    public Value createValue(Calendar value) {
        return new ValueImpl(PropertyType.DATE, value);
    }


    @Override
    public Value createValue(InputStream value) {
        try {
            return createValue(createBinary(value));
        }
        catch (RepositoryException re) {
            return createValue(new BinaryImpl(new byte[0]));
        }
    }


    @Override
    public Value createValue(Binary value) {
        return new ValueImpl(PropertyType.BINARY, value);
    }


    @Override
    public Value createValue(Node value) throws RepositoryException {
        return null;
    }


    @Override
    public Value createValue(Node value, boolean weak) throws RepositoryException {
        return null;
    }


    @Override
    public Binary createBinary(InputStream stream) throws RepositoryException {
        Binary b = new BinaryImpl(stream);
        try {
            stream.close();
        }
        catch (IOException ioe) { /* ignore */ }
        return b;
    }


    public Binary createBinary(String s) {
        return new BinaryImpl(s.getBytes());
    }
}

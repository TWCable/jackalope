package com.twcable.jackalope.impl.jcr;

import org.apache.commons.io.IOUtils;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Simple implementation of an {@link Binary}
 */
public class BinaryImpl implements Binary {
    final byte[] bytes;


    public BinaryImpl(byte[] bytes) {
        this.bytes = bytes;
    }


    public BinaryImpl(InputStream stream) {
        byte[] b = new byte[0];
        try {
            b = IOUtils.toByteArray(stream);
        }
        catch (IOException ioe) { /* ignore */ }
        bytes = b;
    }


    @Override
    public InputStream getStream() throws RepositoryException {
        return new ByteArrayInputStream(bytes);
    }


    @Override
    public int read(byte[] b, long position) throws IOException, RepositoryException {
        int p_int = (int)position;   // Why is position a long and the return an int?
        int length = (b.length < bytes.length - p_int) ? b.length : bytes.length - p_int;
        if (length < 0) return -1;
        System.arraycopy(bytes, p_int, b, 0, length);
        return length;
    }


    @Override
    public long getSize() throws RepositoryException {
        return bytes.length;
    }


    @Override
    public void dispose() {

    }
}

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

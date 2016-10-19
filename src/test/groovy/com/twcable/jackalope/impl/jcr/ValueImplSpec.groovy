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
package com.twcable.jackalope.impl.jcr

import org.apache.commons.io.IOUtils
import spock.lang.Specification
import spock.lang.Subject

import javax.jcr.PropertyType

@Subject(ValueImpl)
@SuppressWarnings("GrDeprecatedAPIUsage")
class ValueImplSpec extends Specification {

    def "ValueImpl holds a jcr value"() {
        def factory = new ValueFactoryImpl()

        expect:
        new ValueImpl(new String("hello, world")).string == "hello, world"
        new ValueImpl(Double.MAX_VALUE).double == Double.MAX_VALUE
        new ValueImpl(Long.MAX_VALUE).long == Long.MAX_VALUE
        new ValueImpl(BigDecimal.TEN).decimal == BigDecimal.TEN
        new ValueImpl(Boolean.TRUE).boolean == Boolean.TRUE
        new ValueImpl(Calendar.instance).date.time.date == Calendar.instance.time.date
        new ValueImpl(PropertyType.STRING, new String("hello, world")).string == "hello, world"
        new ValueImpl(PropertyType.DOUBLE, Double.MAX_VALUE).double == Double.MAX_VALUE
        new ValueImpl(PropertyType.LONG, Long.MAX_VALUE).long == Long.MAX_VALUE
        new ValueImpl(PropertyType.DECIMAL, BigDecimal.TEN).decimal == BigDecimal.TEN
        new ValueImpl(PropertyType.BOOLEAN, Boolean.TRUE).boolean == Boolean.TRUE
        new ValueImpl(PropertyType.DATE, Calendar.instance).date.time.date == Calendar.instance.time.date
        new ValueImpl("hello, world").string == "hello, world"
        new ValueImpl(Double.valueOf(2.5d)).string == "2.5"
        new ValueImpl(Long.valueOf(10L)).string == "10"
        new ValueImpl(BigDecimal.TEN).string == "10"
        new ValueImpl(Boolean.TRUE).string == "true"
        // TODO: Implement correct calendar to string conversion
//        new ValueImpl(Calendar.getInstance()).getString() == ""
        IOUtils.toString(new ValueImpl(PropertyType.BINARY, factory.createBinary("hello, world")).binary.stream) == "hello, world"

        def bytes = new byte[3];
        new ValueImpl(PropertyType.BINARY, factory.createBinary("hello, world")).binary.read(bytes, 1)
        new String(bytes) == "ell"
    }

}

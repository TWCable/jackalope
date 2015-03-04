package com.twcable.jackalope.impl.jcr

import org.apache.commons.io.IOUtils
import spock.lang.Specification
import spock.lang.Subject

import javax.jcr.PropertyType

@Subject(ValueFactoryImpl)
@SuppressWarnings("GroovyPointlessBoolean")
class ValueFactoryImplSpec extends Specification {

    def "creates Values of different Types"() {
        def now = Calendar.getInstance()

        expect:
        new ValueFactoryImpl().createValue("hello").string == "hello"
        new ValueFactoryImpl().createValue(20l).long == 20l;
        new ValueFactoryImpl().createValue(2.1d).double == 2.1d
        new ValueFactoryImpl().createValue(now).date == now
        new ValueFactoryImpl().createValue(false).boolean == false
        new ValueFactoryImpl().createValue(new BigDecimal(2.1)).decimal == new BigDecimal(2.1)

        def binary = new ValueFactoryImpl().createBinary(IOUtils.toInputStream("hello, world", "UTF-8"))
        IOUtils.toString(new ValueFactoryImpl().createValue(binary).binary.stream) == "hello, world"
    }


    def "converts strings to types"() {
        expect:
        new ValueFactoryImpl().createValue("hello", PropertyType.STRING).string == "hello"
        new ValueFactoryImpl().createValue("20", PropertyType.LONG).long == 20l
        new ValueFactoryImpl().createValue("1.5", PropertyType.DOUBLE).double == 1.5d
        new ValueFactoryImpl().createValue("false", PropertyType.BOOLEAN).boolean == false
        new ValueFactoryImpl().createValue("2.5", PropertyType.DECIMAL).decimal == new BigDecimal(2.5)
        IOUtils.toString(new ValueFactoryImpl().createValue("hello, world", PropertyType.BINARY).binary.stream) == "hello, world"
    }

}

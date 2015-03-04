package com.twcable.jackalope.impl.jcr

import spock.lang.Specification
import spock.lang.Subject

import javax.jcr.PropertyType
import javax.jcr.Value
import javax.jcr.ValueFormatException

@Subject(PropertyImpl)
@SuppressWarnings("GrDeprecatedAPIUsage")
class PropertyImplSpec extends Specification {

    def "A property is not a node"() {
        expect:
        !new PropertyImpl(new SessionImpl(), "test").isNode()
    }


    def "A property has a path"() {
        expect:
        new PropertyImpl(new SessionImpl(), "test").path == "test"
    }


    def "Property can have a single value"() {
        def property = new PropertyImpl(new SessionImpl(), "test", (Value)new ValueImpl("hello, world"))

        expect:
        !property.isMultiple()
        property.value.string == "hello, world"
        property.type == PropertyType.STRING

    }


    def "Property can have multiple values"() {
        def property = new PropertyImpl(new SessionImpl(), "test", [new ValueImpl("hello"), new ValueImpl("world")] as Value[])

        expect:
        property.isMultiple()
        property.values[0].string == "hello"
        property.values[1].string == "world"
        property.type == PropertyType.STRING
    }


    def "Property can have multiple String values"() {
        def property = new PropertyImpl(new SessionImpl(), "test", ["hello", "world"] as String[])

        expect:
        property.isMultiple()
        property.values[0].string == "hello"
        property.values[1].string == "world"
    }


    def "The value can be set to any of the primitive values"() {
        def property = new PropertyImpl(new SessionImpl(), "test")

        when:
        property.setValue("hello, world")
        then:
        property.getValue().getString() == "hello, world"
        when:
        property.setValue(new ValueImpl(Double.MAX_VALUE))
        then:
        property.getValue().getDouble() == Double.MAX_VALUE
        when:
        property.setValue(new ValueImpl(Long.MAX_VALUE))
        then:
        property.getValue().getLong() == Long.MAX_VALUE
        when:
        property.setValue(new ValueImpl(BigDecimal.TEN))
        then:
        property.getValue().getDecimal() == BigDecimal.TEN
        when:
        property.setValue(new ValueImpl(Boolean.FALSE))
        then:
        property.getValue().getBoolean() == Boolean.FALSE
        when:
        property.setValue(new ValueImpl(Calendar.getInstance()))
        then:
        property.getValue().getDate().getTime().date == Calendar.getInstance().getTime().date
    }


    def "The length of a single value property is the length of the string representation of that property"() {
        expect:
        new PropertyImpl(new SessionImpl(), "test", value).getLength() == length

        where:
        value                                    | length
        new ValueImpl("hello")                   | 5
        new ValueImpl(Double.valueOf(2.5d))      | 3
        new ValueImpl(Long.valueOf(10l))         | 2
        new ValueImpl(BigDecimal.valueOf(5000l)) | 4
        new ValueImpl(Boolean.TRUE)              | 4
        // Todo: fix calendar strings
    }


    def "getLength throws an exception when the property is multi-valued"() {
        when:
        new PropertyImpl(new SessionImpl(), "test", ["a", "b"] as String[]).length

        then:
        thrown(ValueFormatException)
    }


    def "The length of a multi-value property is the length of the string representation of that property"() {
        expect:
        new PropertyImpl(new SessionImpl(), "test", "a", "bbb").lengths == [1, 3] as long[]
    }


    def "getLengths throws an exception when the property is single value"() {
        when:
        new PropertyImpl(new SessionImpl(), "test", new ValueImpl("hello")).lengths

        then:
        thrown(ValueFormatException)
    }
}

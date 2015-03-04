package com.twcable.jackalope.impl.common

import com.twcable.jackalope.impl.jcr.ValueImpl
import spock.lang.Specification
import spock.lang.Subject

import javax.jcr.Value

@Subject(Values)
class ValuesSpec extends Specification {
    def "Convert values to strings"() {
        expect:
        Values.convertValuesToStrings([new ValueImpl("a"), new ValueImpl("b")] as Value[]) == ["a", "b"] as String[]
    }


    def "Convert strings to values"() {
        expect:
        Values.convertStringsToValues(["a", "b"] as String[]).collect { it.getString() } == ["a", "b"] as String[]
    }


    def "Convert objects to values"() {
        expect:
        Values.convertObjectsToValues(["a", "b"] as Object[]).collect { it.getString() } == ["a", "b"] as String[]
    }
}

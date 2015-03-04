package com.twcable.jackalope.impl.jcr

import com.google.common.collect.Lists
import spock.lang.Specification
import spock.lang.Subject

@Subject(PropertyIteratorImpl)
class PropertyIteratorImplSpec extends Specification {

    def "A PropertyIterator iterates over a list of Properties"() {
        def session = new SessionImpl()

        when:
        List<PropertyImpl> actual = Lists.newArrayList(new PropertyIteratorImpl([
            new PropertyImpl(session, "first", new ValueImpl("a")),
            new PropertyImpl(session, "second", new ValueImpl("b")),
            new PropertyImpl(session, "third", new ValueImpl("c"))]))

        then:
        actual.find { it.name == "first" }.string == "a"
        actual.find { it.name == "second" }.string == "b"
        actual.find { it.name == "third" }.string == "c"
        actual.size() == 3
    }

}

package com.twcable.jackalope.impl.jcr

import com.google.common.collect.Lists
import spock.lang.Specification
import spock.lang.Subject

@Subject(NodeImpl)
class NodeIteratorImpSpec extends Specification {

    def "A Node iterates over a list of Properties"() {
        def session = new SessionImpl()
        def node1 = new NodeImpl(session, "first")
        node1.setProperty("prop", "a")
        def node2 = new NodeImpl(session, "second")
        node2.setProperty("prop", "b")
        def node3 = new NodeImpl(session, "third")
        node3.setProperty("prop", "c")

        when:
        List<NodeImpl> actual = Lists.newArrayList(new NodeIteratorImpl([node1, node2, node3]))

        then:
        actual.find { it.name == "first" }.getProperty("prop").string == "a"
        actual.find { it.name == "second" }.getProperty("prop").string == "b"
        actual.find { it.name == "third" }.getProperty("prop").string == "c"
        actual.size() == 3
    }

}

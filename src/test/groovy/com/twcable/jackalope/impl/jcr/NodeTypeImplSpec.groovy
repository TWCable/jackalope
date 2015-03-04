package com.twcable.jackalope.impl.jcr

import spock.lang.Specification
import spock.lang.Subject

@Subject(NodeTypeImpl)
class NodeTypeImplSpec extends Specification {

    def "NodeType has a name"() {
        expect:
        new NodeTypeImpl("name").getName() == "name"
    }


    def "NodeType is tested by name"() {
        expect:
        new NodeTypeImpl("nodetype").isNodeType("nodetype")
        !new NodeTypeImpl("nodetype").isNodeType("nodetypex")
    }

}

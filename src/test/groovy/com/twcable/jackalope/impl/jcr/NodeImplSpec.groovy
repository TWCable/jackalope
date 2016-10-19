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

import com.google.common.collect.Lists
import spock.lang.Specification
import spock.lang.Subject

import javax.jcr.Value
import javax.jcr.nodetype.NodeType

@Subject(NodeImpl)
class NodeImplSpec extends Specification {
    def "A new node can be created"() {
        when:
        def node = new NodeImpl(new SessionImpl(), "test")

        then:
        node.isNode()
        node.getPath() == "test"
    }


    def "Properties can be set on nodes"() {
        def node = new NodeImpl(new SessionImpl(), "test")
        node.getSession().save()

        when:
        node.setProperty("prop", "hello")
        then:
        node.isModified()
        node.hasProperties()
        node.hasProperty("prop")
        node.getProperty("prop").getString() == "hello"
        node.getProperty("prop").isNew()
        when:
        node.setProperty("prop", 2.5d)
        then:
        node.getProperty("prop").getDouble().doubleValue() == 2.5d
        when:
        node.setProperty("prop", 100l)
        then:
        node.getProperty("prop").getLong().longValue() == 100l
        when:
        node.setProperty("prop", BigDecimal.valueOf(5000l))
        then:
        node.getProperty("prop").getDecimal().longValue() == 5000l
        when:
        node.setProperty("prop", Boolean.TRUE)
        then:
        node.getProperty("prop").getBoolean() == Boolean.TRUE
        when:
        node.setProperty("prop", [new ValueImpl("a"), new ValueImpl("b"), new ValueImpl("c")] as Value[])
        then:
        node.getProperty("prop").isMultiple()
        node.getProperty("prop").getValues().length == 3
        node.getProperty("prop").getValues()[0].getString() == "a"
        node.getProperty("prop").getValues()[1].getString() == "b"
        node.getProperty("prop").getValues()[2].getString() == "c"
        when:
        node.setProperty("prop", ["a", "b", "c"] as String[])
        then:
        node.getProperty("prop").isMultiple()
        node.getProperty("prop").getValues().length == 3
        node.getProperty("prop").getValues()[0].getString() == "a"
        node.getProperty("prop").getValues()[1].getString() == "b"
        node.getProperty("prop").getValues()[2].getString() == "c"
    }


    def "Nodes have a set of properties"() {
        def node = new NodeImpl(new SessionImpl(), "test")
        node.setProperty("first", "a")
        node.setProperty("second", "b")
        node.setProperty("third", "c")

        when:
        def properties = Lists.newArrayList(node.getProperties())

        then:
        properties.find { it.getName() == "first" }.getString() == "a"
        properties.find { it.getName() == "second" }.getString() == "b"
        properties.find { it.getName() == "third" }.getString() == "c"
        properties.size() == 3
    }


    def "Child nodes can be created"() {
        def node = new NodeImpl(new SessionImpl(), "test")
        node.getSession().save()

        when:
        def child = node.addNode("child")

        then:
        node.hasNodes()
        node.hasNode("child")
        child.isNode()
        child.isNew()
        node.isModified()
        node.getNode("child") == child
        node.getNode("child").isNodeType(NodeType.NT_UNSTRUCTURED)
    }


    def "Child nodes can be created with a specific node type"() {
        def node = new NodeImpl(new RepositoryImpl().login(), "test")
        node.getSession().save()

        when:
        node.addNode("child", NodeType.NT_FOLDER)

        then:
        node.getNode("child").isNodeType(NodeType.NT_FOLDER)
    }


    def "A node can have multiple child nodes"() {
        def node = new NodeImpl(new SessionImpl(), "test")
        node.addNode("first")
        node.addNode("second")
        node.addNode("third")
        node.getSession().save()

        when:
        def nodes = Lists.newArrayList(node.getNodes())

        then:
        nodes.find { it.getName() == "first" }
        nodes.find { it.getName() == "second" }
        nodes.find { it.getName() == "third" }
        nodes.size() == 3
    }


    def "A node can be saved"() {
        when:
        def node = new NodeImpl(new SessionImpl(), "test")
        then:
        node.isNew()
        node.getSession().hasPendingChanges()

        when:
        node.save()
        then:
        !node.isNew()
        !node.getSession().hasPendingChanges()

        when:
        node.setProperty("prop", "a")
        then:
        node.isModified()
        node.getSession().hasPendingChanges()

        when:
        node.save()
        then:
        !node.isModified()
        !node.getSession().save()
    }


    def "Saving a node saves its children"() {
        when:
        def node = new NodeImpl(new SessionImpl(), "test")
        def child = node.addNode("child")
        then:
        node.isNew()
        child.isNew()
        node.getSession().hasPendingChanges()

        when:
        node.save()
        then:
        !node.isNew()
        !child.isNew()
        !node.getSession().hasPendingChanges()

        when:
        child.setProperty("prop", "a")
        then:
        child.isModified()
        node.getSession().hasPendingChanges()

        when:
        node.save()
        then:
        !child.isModified()
        !node.getSession().save()
    }


    def "Saving a node does not save any other node"() {
        def session = new SessionImpl()

        when:
        def node1 = new NodeImpl(session, "node1")
        def node2 = new NodeImpl(session, "node2")
        then:
        node1.isNew()
        node2.isNew()
        session.hasPendingChanges()

        when:
        node1.save()
        then:
        !node1.isNew()
        node2.isNew()
        session.hasPendingChanges()

        when:
        node2.save()
        then:
        !node2.isNew()
        !session.hasPendingChanges()

        when:
        node1.setProperty("prop", "a")
        node2.setProperty("prop", "b")
        then:
        node1.isModified()
        node2.isModified()
        session.hasPendingChanges()

        when:
        node1.save()
        then:
        !node1.isModified()
        node2.isModified()
        session.hasPendingChanges()
    }


    def "remove() deletes the node from the session"() {
        def session = new SessionImpl()
        def parent = new NodeImpl(session, "parent")
        def child = parent.addNode("child")
        session.nodeExists("parent/child")

        when:
        child.remove()

        then:
        !session.nodeExists("parent/child")
    }


    def "remove deletes descendent nodes from the session"() {
        def session = new SessionImpl()
        def parent = new NodeImpl(session, "parent")
        def child = parent.addNode("child")
        def grandchild = child.addNode("grandchild")
        def greatgrandchild = grandchild.addNode("greatgrandchild")
        session.nodeExists("parent/child")
        session.nodeExists("parent/child/grandchild")
        session.nodeExists("parent/child/grandchild/greatgrandchild")

        when:
        child.remove()

        then:
        !session.nodeExists("parent/child")
        !session.nodeExists("parent/child/grandchild")
        !session.nodeExists("parent/child/grandchild/greatgrandchild")
    }


    def "getParent for a standalone node returns the virtual root"() {
        def session = new SessionImpl()
        def node = new NodeImpl(session, "node")

        when:
        def parent = node.getParent()

        then:
        parent == session.getRootNode()
    }
}

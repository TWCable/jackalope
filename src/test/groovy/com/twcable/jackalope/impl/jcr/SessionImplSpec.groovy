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

import spock.lang.Specification
import spock.lang.Subject

class SessionImplSpec extends Specification {

    @Subject
    SessionImpl session = new SessionImpl()


    def "Nodes can be added and retrieved"() {
        when:
        def node = new NodeImpl(session, "test")

        then:
        session.nodeExists("test")
        session.itemExists("test")
        session.getNode("test") == node
    }


    def "Properties can be added and retrieved"() {
        when:
        def node = new NodeImpl(session, "test")
        node.setProperty("prop1", "a")
        node.setProperty("prop2", "a")

        then:
        session.propertyExists("test/prop1")
        session.propertyExists("test/prop2")
        session.getProperty("test/prop1").getString() == "a"
        session.getProperty("test/prop2").getString() == "a"
    }


    def "Session tracks added and changing nodes"() {
        when:
        def node = new NodeImpl(session, "test")

        then:
        session.isNew(node)
        session.hasPendingChanges()

        when:
        node.save()

        then:
        !session.isNew(node)
        !session.hasPendingChanges()

        when:
        node.setProperty("prop", "a")

        then:
        session.isModified(node)
        session.hasPendingChanges()

        when:
        session.save()

        then:
        !session.isModified(node)
        !session.hasPendingChanges()
    }


    def "Removing an item removes its descendents"() {
        def parent = new NodeImpl(session, "parent")
        def child = parent.addNode("child")
        def grandchild = child.addNode("grandchild")
        grandchild.addNode("greatgrandchild")

        expect:
        session.nodeExists("parent/child")
        session.nodeExists("parent/child/grandchild")
        session.nodeExists("parent/child/grandchild/greatgrandchild")

        when:
        session.removeItem(child.getPath())

        then:
        !session.nodeExists("parent/child")
        !session.nodeExists("parent/child/grandchild")
        !session.nodeExists("parent/child/grandchild/greatgrandchild")
    }


    @SuppressWarnings("GroovyUnusedAssignment")
    def "A node can be moved"() {
        def dest = new NodeImpl(session, "/dest")
        def src = new NodeImpl(session, "/src")
        src.addNode("child")

        when:
        session.move("/src", "/dest/target")

        then:
        session.nodeExists("/dest/target")
        session.nodeExists("/dest/target/child")
    }

}

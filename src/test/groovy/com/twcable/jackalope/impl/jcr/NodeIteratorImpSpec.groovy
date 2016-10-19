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

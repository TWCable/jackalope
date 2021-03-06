/*
 * Copyright 2015 Time Warner Cable, Inc.
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

package com.twcable.jackalope.impl.sling

import com.day.cq.dam.api.Asset
import com.google.common.collect.Lists
import com.twcable.jackalope.impl.jcr.NodeImpl
import com.twcable.jackalope.impl.jcr.SessionImpl
import com.twcable.jackalope.impl.jcr.ValueFactoryImpl
import org.apache.commons.io.IOUtils
import org.apache.sling.api.resource.ValueMap
import spock.lang.Specification
import spock.lang.Subject

import javax.jcr.Node

import static com.twcable.jackalope.JCRBuilder.node
import static com.twcable.jackalope.JCRBuilder.property
import static com.twcable.jackalope.JCRBuilder.resource
import static org.apache.jackrabbit.JcrConstants.JCR_PRIMARYTYPE
import static org.apache.sling.jcr.resource.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY

@Subject(NodeResourceImpl)
class NodeResourceImplSpec extends Specification {
    SlingRepositoryImpl repository
    SessionImpl session


    def setup() {
        repository = new SlingRepositoryImpl()
        session = repository.login() as SessionImpl
    }


    def "Create a Node resource from a simple node"() {
        def node = new NodeImpl(session, "/test")
        node.setProperty(SLING_RESOURCE_TYPE_PROPERTY, "app/test/components/test")
        def resourceResolver = new ResourceResolverImpl(repository)

        when:
        def resource = new NodeResourceImpl(resourceResolver, node)

        then:
        resource.name == "test"
        resource.path == "/test"
        resource.parent.path == "/"
        resource.resourceResolver == resourceResolver
        resource.resourceType == "app/test/components/test"
        resource.isResourceType("app/test/components/test")
    }


    def "Create child resources"() {
        def node = new NodeImpl(session, "/test")
        node.addNode("child1")
        node.addNode("child2")
        def resourceResolver = new ResourceResolverImpl(repository)

        when:
        def resource = new NodeResourceImpl(resourceResolver, node)

        then:
        resource.getChild("child1").name == "child1"
        resource.getChild("child1").path == "/test/child1"
        resource.getChild("child1").parent.name == "test"
        resource.getChild("child2").name == "child2"
        resource.getChild("child2").path == "/test/child2"
        resource.getChild("child2").parent.name == "test"

        when:
        def actual = Lists.newArrayList(resource.listChildren())

        then:
        actual.find { it.name == "child1" }
        actual.find { it.name == "child2" }
    }


    def "NodeResource can be adapted to Node"() {
        def resource = new NodeResourceImpl(new ResourceResolverImpl(repository), new NodeImpl(session, "/test"))
        def node = resource.adaptTo(Node)

        expect:
        node.name == "test"
        node.path == "/test"
    }


    def "NodeResource can be adapted to a ValueMap"() {
        def node = new NodeImpl(session, "/test")
        node.setProperty("string", "hello")
        node.setProperty("long", Long.valueOf(1000l))
        def resource = new NodeResourceImpl(new ResourceResolverImpl(repository), node)
        def map = resource.adaptTo(ValueMap)

        expect:
        map.get("string") == "hello"
        map.get("long") == Long.valueOf(1000l)
    }


    def "it can be adapted to an Asset"() {
        def resource = resource(
            node("/test.csv",
                property(JCR_PRIMARYTYPE, "dam:Asset"),
                node("jcr:content",
                    property(JCR_PRIMARYTYPE, "dam:AssetContent"),
                    node("renditions",
                        property(JCR_PRIMARYTYPE, "nt:folder"),
                        node("original",
                            property(JCR_PRIMARYTYPE, "nt:file"),
                            node("jcr:content",
                                property("jcr:mimeType", "text/csv"))))))).build()
        when:
        def asset = resource.adaptTo(Asset)

        then:
        asset != null
        asset instanceof Asset
    }


    def "it can be adapted to an InputStream"() {
        def resource = resource(node("original",
            property(JCR_PRIMARYTYPE, "nt:unstructured"),
            property("jcr:data", new ValueFactoryImpl().createBinary("hello, world")))).build()
        when:
        def stream = resource.adaptTo(InputStream)

        then:
        stream != null
        IOUtils.toString(stream) == "hello, world"
    }


    def "it uses the jcr:data property of the jcr:content node when the primary type is nt:file and it is adapted to an InputStream"() {
        def resource = resource(node("original",
            property(JCR_PRIMARYTYPE, "nt:file"),
            node("jcr:content",
                property("jcr:data", new ValueFactoryImpl().createBinary("hello, world"))))).build()
        when:
        def stream = resource.adaptTo(InputStream)

        then:
        stream != null
        IOUtils.toString(stream) == "hello, world"
    }


    def "returns null when adapted to an InputStream and no jcr:data"() {
        def resource = resource(node("original",
            property(JCR_PRIMARYTYPE, "nt:unstructured"))).build()
        when:
        def stream = resource.adaptTo(InputStream)

        then:
        stream == null
    }

}

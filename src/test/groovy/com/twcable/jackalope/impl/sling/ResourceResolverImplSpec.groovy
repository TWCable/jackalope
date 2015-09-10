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

import com.twcable.jackalope.impl.jcr.NodeImpl
import com.twcable.jackalope.impl.jcr.SessionImpl
import org.apache.sling.api.resource.ValueMap
import spock.lang.Specification
import spock.lang.Subject

class ResourceResolverImplSpec extends Specification {

    SlingRepositoryImpl repository
    SessionImpl session

    @Subject
    ResourceResolverImpl resourceResolver


    def setup() {
        repository = new SlingRepositoryImpl()
        session = repository.login() as SessionImpl
        resourceResolver = new ResourceResolverImpl(repository)
    }


    def "Resolves node resources"() {
        //noinspection GroovyUnusedAssignment
        def node = new NodeImpl(session, "/test")

        when:
        def resolved = resourceResolver.resolve("/test")

        then:
        resolved.name == "test"
        resolved.path == "/test"
        resolved instanceof NodeResourceImpl

        when:
        def resource = resourceResolver.getResource("/test")

        then:
        resource.name == "test"
        resource.path == "/test"

        when:
        def resourceChild = resourceResolver.getResource(resourceResolver.getResource("/"), "test")

        then:
        resourceChild.name == "test"
        resourceChild.path == "/test"
    }


    def "Resolves property resources"() {
        def node = new NodeImpl(session, "/test")
        node.setProperty("prop", "hello, world")

        when:
        def resolved = resourceResolver.resolve("/test/prop")

        then:
        resolved.name == "prop"
        resolved.path == "/test/prop"
        resolved instanceof PropertyResourceImpl

        when:
        def resource = resourceResolver.getResource("/test/prop")

        then:
        resource.name == "prop"
        resource.path == "/test/prop"

        when:
        def resourceProp = resourceResolver.getResource(resourceResolver.getResource("/test"), "prop")

        then:
        resourceProp.name == "prop"
        resourceProp.path == "/test/prop"
    }


    def "Creates node resources"() {
        //noinspection GroovyUnusedAssignment
        def parentNode = new NodeImpl(session, "/test")
        def parent = resourceResolver.getResource("/test")

        when:
        def created = resourceResolver.create(parent, "child", [prop1: "val1", prop2: "val2"])

        then:
        created.name == "child"
        created.path == "/test/child"
        created.adaptTo(ValueMap).keySet() == ["prop1", "prop2"] as Set
    }


    def "Deletes node resources"() {
        def path = "/test"

        //noinspection GroovyUnusedAssignment
        def parentNode = new NodeImpl(session, path)
        def resource = resourceResolver.getResource(path)

        expect:
        resourceResolver.getResource(path) != null

        when:
        resourceResolver.delete(resource)

        then:
        resourceResolver.getResource(path) == null
    }

}

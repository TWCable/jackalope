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
import spock.lang.Specification
import spock.lang.Subject

@Subject(ResourceResolverImpl)
class ResourceResolverImplSpec extends Specification {

    @SuppressWarnings("GroovyUnusedAssignment")
    def "Resolves node resources"() {
        def repository = new SlingRepositoryImpl()
        def session = repository.login() as SessionImpl
        def node = new NodeImpl(session, "/test")
        def resourceResolver = new ResourceResolverImpl(repository)

        when:
        def resource = resourceResolver.resolve("/test")

        then:
        resource.name == "test"
        resource.path == "/test"
        resource instanceof NodeResourceImpl

        when:
        resource = resourceResolver.getResource("/test")

        then:
        resource.name == "test"
        resource.path == "/test"

        when:
        resource = resourceResolver.getResource(resourceResolver.getResource("/"), "test")

        then:
        resource.name == "test"
        resource.path == "/test"
    }


    def "Resolves property resources"() {
        def repository = new SlingRepositoryImpl()
        def session = repository.login() as SessionImpl
        def node = new NodeImpl(session, "/test")
        node.setProperty("prop", "hello, world")
        def resourceResolver = new ResourceResolverImpl(repository)

        when:
        def resource = resourceResolver.resolve("/test/prop")

        then:
        resource.name == "prop"
        resource.path == "/test/prop"
        resource instanceof PropertyResourceImpl

        when:
        resource = resourceResolver.getResource("/test/prop")

        then:
        resource.name == "prop"
        resource.path == "/test/prop"

        when:
        resource = resourceResolver.getResource(resourceResolver.getResource("/test"), "prop")

        then:
        resource.name == "prop"
        resource.path == "/test/prop"
    }

}

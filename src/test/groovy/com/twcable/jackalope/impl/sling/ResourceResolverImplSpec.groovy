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

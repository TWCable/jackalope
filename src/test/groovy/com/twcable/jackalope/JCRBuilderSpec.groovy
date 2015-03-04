package com.twcable.jackalope

import com.day.cq.wcm.api.PageManager
import com.twcable.jackalope.impl.jcr.NodeImpl
import com.twcable.jackalope.impl.jcr.SessionImpl
import com.twcable.jackalope.impl.sling.ResourceResolverImpl
import com.twcable.jackalope.impl.sling.SimpleResourceResolverFactory
import com.twcable.jackalope.impl.sling.SlingRepositoryImpl
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ValueMap
import spock.lang.Specification
import spock.lang.Subject

import javax.jcr.Node
import javax.jcr.Property

import static com.twcable.jackalope.JCRBuilder.node
import static com.twcable.jackalope.JCRBuilder.property
import static com.twcable.jackalope.JCRBuilder.repository
import static com.twcable.jackalope.JCRBuilder.resource
import static com.twcable.jackalope.JcrConstants.CQ_PAGE
import static com.twcable.jackalope.JcrConstants.CQ_PAGE_CONTENT
import static com.twcable.jackalope.JcrConstants.NT_FILE
import static com.twcable.jackalope.JcrConstants.NT_UNSTRUCTURED

@Subject(JCRBuilder)
@SuppressWarnings("GroovyAccessibility")
class JCRBuilderSpec extends Specification {

    def "Build a single value property"() {
        def property = property("prop", "hello").build(new NodeImpl(new SessionImpl(), "test"))

        expect:
        property instanceof Property
        !property.multiple
        property.name == "prop"
        property.path == "test/prop"
        property.string == "hello"
    }


    def "Build a multi-value property"() {
        def property = property("prop", ["hello", "world"] as String[]).build(new NodeImpl(new SessionImpl(), "test"))

        expect:
        property instanceof Property
        property.multiple
        property.name == "prop"
        property.path == "test/prop"
        property.values[0].string == "hello"
        property.values[1].string == "world"
    }


    def "Build a multi-value double property"() {
        def doubles = [new Double(1.0), new Double(2.0)] as Double[]
        def property = property("prop", doubles).build(new NodeImpl(new SessionImpl(), "test"))

        expect:
        property instanceof Property
        property.multiple
        property.name == "prop"
        property.path == "test/prop"
        property.values[0].double == 1.0
        property.values[1].double == 2.0
    }


    def "Build a simple node"() {
        def node = node("node").build()

        expect:
        node instanceof Node
        node.name == "node"
        node.path == "node"
        node.isNodeType(NT_UNSTRUCTURED)
    }


    def "Build a node of a specific type"() {
        def node = node("node", NT_FILE).build()

        expect:
        node instanceof Node
        node.name == "node"
        node.path == "node"
        node.isNodeType(NT_FILE)
    }


    def "Build a node with child nodes and properties"() {
        def node = node("node",
            property("prop1", "a"),
            property("prop2", "b"),
            node("child1"),
            node("child2")).build()

        expect:
        node.name == "node"
        node.path == "node"
        node.getNode("child1").path == "node/child1"
        node.getNode("child2").path == "node/child2"
        node.getProperty("prop1").string == "a"
        node.getProperty("prop2").string == "b"
    }


    def "Build a resource with child resources and properties"() {
        def resource = resource(node("resource",
            property("prop1", "a"),
            property("prop2", "b"),
            node("child1"),
            node("child2"))).build()

        expect:
        resource.name == "resource"
        resource.path == "resource"
        resource.getChild("child1").path == "resource/child1"
        resource.getChild("child2").path == "resource/child2"
        resource.adaptTo(ValueMap).get("prop1", "") == "a"
        resource.adaptTo(ValueMap).get("prop2", "") == "b"
    }


    def "Create a page manually..."() {
        def repository = repository(node("page", CQ_PAGE, node("jcr:content", CQ_PAGE_CONTENT,
            property("jcr:title", "title"),
            property("pageTitle", "pageTitle")))).build();
        def manager = new ResourceResolverImpl(repository).adaptTo(PageManager)
        def page = manager.getPage("/page")

        expect:
        page != null
        page.path == "/page"
        page.contentResource.name == "jcr:content"
        page.title == "title"
        page.pageTitle == "pageTitle"
    }


    def "...or, use PageManager to create a page."() {
        def resolver = new SimpleResourceResolverFactory(new SlingRepositoryImpl()).getAdministrativeResourceResolver([:])
        def manager = resolver.adaptTo(PageManager.class)
        def page = manager.create("/content/test1", "page1", null, "title")

        expect:
        page.path == "/content/test1/page1"
        page.name == "page1"
        page.title == "title"
    }


    def "Page's jcr:content is a resource!"() {
        def repository = repository(
            node("page", CQ_PAGE, node("jcr:content", CQ_PAGE_CONTENT,
                property("jcr:title", "title")))).build();
        def manager = new ResourceResolverImpl(repository).adaptTo(PageManager)
        def page = manager.getPage("/page")

        expect:
        page.contentResource instanceof Resource
    }


    def "PageManager can copy (like a real PageManager)"() {
        def repository = repository(node("content", node("test1",
            node("page1", CQ_PAGE, node("jcr:content", CQ_PAGE_CONTENT,
                node("anotherResource"),
                property("jcr:title", "title")))))).build()
        def resolver = new SimpleResourceResolverFactory(repository).resourceResolver
        def manager = resolver.adaptTo(PageManager.class)

        def page = manager.getPage("/content/test1/page1")
        def pageCopy = manager.copy(page, "/content/test1/copy", null, true, false)

        expect:
        page != null
        pageCopy != null
        pageCopy.path == "/content/test1/copy/page1"
        pageCopy.title == "title"
        pageCopy.contentResource.adaptTo(Node).primaryNodeType.name.equals(CQ_PAGE_CONTENT)
        pageCopy.contentResource.getChild("anotherResource") != null
    }


    def "PageManager can delete."() {
        def repository = repository(node("content", node("test1",
            node("page1", CQ_PAGE, node("jcr:content", CQ_PAGE_CONTENT, property("jcr:title", "title")))))).build()
        def resolver = new SimpleResourceResolverFactory(repository).resourceResolver
        def manager = resolver.adaptTo(PageManager)

        def page = manager.getPage("/content/test1/page1")
        manager.delete(page, false);

        expect:
        manager.getPage("/content/test1/page1") == null
    }


    def "PageManager can delete recursively."() {
        def repository = repository(node("content", node("test1",
            node("page1", CQ_PAGE, node("jcr:content", CQ_PAGE_CONTENT, property("jcr:title", "title")),
                node("page2", CQ_PAGE, node("jcr:content", CQ_PAGE_CONTENT, property("jcr:title", "title2"))))))).build()
        def resolver = new SimpleResourceResolverFactory(repository).resourceResolver
        def manager = resolver.adaptTo(PageManager.class)

        def page = manager.getPage("/content/test1/page1")
        def childPage = manager.getPage("/content/test1/page1/page2")
        manager.delete(page, false);

        expect:
        page != null
        childPage != null
        manager.getPage("/content/test1/page1/page2") == null
        manager.getPage("/content/test1/page1") == null
    }


    def "Build a repository"() {
        def repository = repository(node("node",
            property("prop1", "a"),
            property("prop2", "b"),
            node("child1"),
            node("child2"))).build()
        def resolver = new SimpleResourceResolverFactory(repository).getAdministrativeResourceResolver([:])
        def resource = resolver.getResource("/node")

        expect:
        resolver.getResource("/node")
        resource.name == "node"
        resource.path == "/node"
        resource.getChild("child1").path == "/node/child1"
        resource.getChild("child2").path == "/node/child2"
        resource.adaptTo(ValueMap).get("prop1") == "a"
        resource.adaptTo(ValueMap).get("prop2") == "b"
    }

}

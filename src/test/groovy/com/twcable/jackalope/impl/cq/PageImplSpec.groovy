package com.twcable.jackalope.impl.cq

import com.twcable.jackalope.impl.jcr.NodeImpl
import com.twcable.jackalope.impl.jcr.SessionImpl
import com.twcable.jackalope.impl.sling.ResourceResolverImpl
import com.twcable.jackalope.impl.sling.SlingRepositoryImpl
import spock.lang.Specification
import spock.lang.Subject

@Subject(PageImpl)
class PageImplSpec extends Specification {
    SlingRepositoryImpl repository
    SessionImpl session

    def setup() {
        repository = new SlingRepositoryImpl()
        session = repository.login() as SessionImpl
    }

    def "isHideInNav() returns correct value"() {
        def pageNode = new NodeImpl(session, "/page")
        def resourceResolver = new ResourceResolverImpl(repository)
        def page = new PageImpl(resourceResolver.getResource("/page"))

        when:
        def content = pageNode.addNode("jcr:content", "cq:PageContent")

        then:
        !page.isHideInNav()

        when:
        content.setProperty("hideInNav", true)

        then:
        page.isHideInNav()
    }

    def "isValid() returns correct value"() {
        def pageNode = new NodeImpl(session, "/page")
        def resourceResolver = new ResourceResolverImpl(repository)
        def page = new PageImpl(resourceResolver.getResource("/page"))

        when:
        def content = pageNode.addNode("jcr:content", "cq:PageContent")

        then:
        page.isValid()

        when:
        content.setProperty("onTime", new GregorianCalendar(2099, 5, 15))

        then:
        !page.isValid()

        when:
        content.setProperty("onTime", new GregorianCalendar(1900, 5, 15))

        then:
        page.isValid()

        when:
        content.setProperty("offTime", new GregorianCalendar(1900, 6, 15))

        then:
        !page.isValid()

        when:
        content.setProperty("offTime", new GregorianCalendar(2099, 6, 15))

        then:
        page.isValid()

    }
}

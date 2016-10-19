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

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
package com.twcable.jackalope.impl.sling

import com.twcable.jackalope.impl.jcr.NodeImpl
import com.twcable.jackalope.impl.jcr.PropertyImpl
import com.twcable.jackalope.impl.jcr.SessionImpl
import com.twcable.jackalope.impl.jcr.ValueImpl
import spock.lang.Specification
import spock.lang.Subject

import static org.apache.sling.jcr.resource.JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY

@Subject(PropertyResourceImpl)
class PropertyResourceImplSpec extends Specification {

    def "A PropertyResource can be adapted to its various value types"() {
        def now = Calendar.getInstance()
        def session = new SessionImpl()
        def resolver = new ResourceResolverImpl(new SlingRepositoryImpl())
        def stringp = new PropertyResourceImpl(resolver, new PropertyImpl(session, "string", new ValueImpl("hello")))
        def longp = new PropertyResourceImpl(resolver, new PropertyImpl(session, "long", new ValueImpl(Long.valueOf(1000l))))
        def doublep = new PropertyResourceImpl(resolver, new PropertyImpl(session, "double", new ValueImpl(Double.valueOf(5000d))))
        def decimalp = new PropertyResourceImpl(resolver, new PropertyImpl(session, "decimal", new ValueImpl(BigDecimal.valueOf(2000d))))
        def booleanp = new PropertyResourceImpl(resolver, new PropertyImpl(session, "boolean", new ValueImpl(Boolean.TRUE)))
        def datep = new PropertyResourceImpl(resolver, new PropertyImpl(session, "date", new ValueImpl(now)))

        expect:
        stringp.adaptTo(String) instanceof String
        stringp.adaptTo(String) == "hello"
        longp.adaptTo(Long) instanceof Long
        longp.adaptTo(Long) == Long.valueOf(1000l)
        doublep.adaptTo(Double) instanceof Double
        doublep.adaptTo(Double) == Double.valueOf(5000d)
        decimalp.adaptTo(BigDecimal) instanceof BigDecimal
        decimalp.adaptTo(BigDecimal) == BigDecimal.valueOf(2000d)
        booleanp.adaptTo(Boolean) instanceof Boolean
        booleanp.adaptTo(Boolean) == Boolean.TRUE
        datep.adaptTo(Calendar) instanceof Calendar
        datep.adaptTo(Calendar) == now
    }


    def "getResourceType returns the resource type of the containing node plus the property name"() {
        def repository = new SlingRepositoryImpl()
        def session = repository.login() as SessionImpl
        def node = new NodeImpl(session, "/test")
        node.setProperty(SLING_RESOURCE_TYPE_PROPERTY, "app/test/components/test")
        node.setProperty("string", "hello")

        when:
        def resource = new PropertyResourceImpl(new ResourceResolverImpl(repository), node.getProperty("string"))

        then:
        resource.getResourceType() == "app/test/components/test/string"
    }

}

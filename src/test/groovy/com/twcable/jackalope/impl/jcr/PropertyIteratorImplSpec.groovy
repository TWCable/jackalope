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

package com.twcable.jackalope.impl.jcr

import com.google.common.collect.Lists
import spock.lang.Specification
import spock.lang.Subject

@Subject(PropertyIteratorImpl)
class PropertyIteratorImplSpec extends Specification {

    def "A PropertyIterator iterates over a list of Properties"() {
        def session = new SessionImpl()

        when:
        List<PropertyImpl> actual = Lists.newArrayList(new PropertyIteratorImpl([
            new PropertyImpl(session, "first", new ValueImpl("a")),
            new PropertyImpl(session, "second", new ValueImpl("b")),
            new PropertyImpl(session, "third", new ValueImpl("c"))]))

        then:
        actual.find { it.name == "first" }.string == "a"
        actual.find { it.name == "second" }.string == "b"
        actual.find { it.name == "third" }.string == "c"
        actual.size() == 3
    }

}

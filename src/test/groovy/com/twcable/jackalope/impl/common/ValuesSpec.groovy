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
package com.twcable.jackalope.impl.common

import com.twcable.jackalope.impl.jcr.ValueImpl
import spock.lang.Specification
import spock.lang.Subject

import javax.jcr.Value

@Subject(Values)
class ValuesSpec extends Specification {
    def "Convert values to strings"() {
        expect:
        Values.convertValuesToStrings([new ValueImpl("a"), new ValueImpl("b")] as Value[]) == ["a", "b"] as String[]
    }


    def "Convert strings to values"() {
        expect:
        Values.convertStringsToValues(["a", "b"] as String[]).collect { it.getString() } == ["a", "b"] as String[]
    }


    def "Convert objects to values"() {
        expect:
        Values.convertObjectsToValues(["a", "b"] as Object[]).collect { it.getString() } == ["a", "b"] as String[]
    }
}

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

package com.twcable.jackalope.impl

import com.google.common.collect.Lists
import com.twcable.jackalope.JCRBuilder
import com.twcable.jackalope.JCRQueryBuilder
import spock.lang.Specification
import spock.lang.Subject

import static com.twcable.jackalope.JCRQueryBuilder.query
import static com.twcable.jackalope.JCRQueryBuilder.queryManager
import static com.twcable.jackalope.JCRQueryBuilder.result

@Subject(JCRQueryBuilder)
class JCRQueryBuilderSpec extends Specification {

    def "Constructing a QueryManager attaches it to its associated session"() {
        def node = JCRBuilder.node("content").build()

        when:
        def queryManager = queryManager(node.session).build()

        then:
        queryManager != null
        node.session.workspace.queryManager == queryManager
    }


    def "Construct a QueryManager with a query and results"() {
        def node = JCRBuilder.node("content").build()

        when:
        queryManager(node.session, query("query", "language", result(node))).build()
        def results = Lists.newArrayList(node.session.workspace.queryManager.createQuery("query", "language").execute().nodes)

        then:
        results.size() == 1
        results[0] == node
    }

}

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

package com.twcable.jackalope.impl.cq

import spock.lang.Specification
import spock.lang.Subject

import static com.twcable.jackalope.JCRBuilder.node
import static com.twcable.jackalope.JCRBuilder.property
import static com.twcable.jackalope.JCRBuilder.resource
import static org.apache.jackrabbit.JcrConstants.JCR_PRIMARYTYPE

@Subject(AssetImpl)
class AssetSpec extends Specification {
    def "getOriginal returns the original rendition"() {
        def resource = resource(
            node("/test.csv",
                property(JCR_PRIMARYTYPE, "dam:Asset"),
                node("jcr:content",
                    property(JCR_PRIMARYTYPE, "dam:AssetContent"),
                    node("renditions",
                        property(JCR_PRIMARYTYPE, "nt:folder"),
                        node("original",
                            property(JCR_PRIMARYTYPE, "nt:file"),
                            node("jcr:content",
                                property("jcr:mimeType", "text/csv"))))))).build()
        def asset = new AssetImpl(resource)

        when: "getOriginal is called"
        def original = asset.getOriginal()

        then:
        original.getProperties().get("jcr:mimeType") == "text/csv"
        original.getChild("jcr:content") != null
    }
}

package com.twcable.jackalope.impl.common

import spock.lang.Specification
import spock.lang.Subject

@Subject(Paths)
class PathsSpec extends Specification {

    def "head returns the first segment of the path"() {
        expect:
        Paths.head(input) == expected

        where:
        input          | expected
        ""             | ""
        "/"            | ""
        "segment"      | "segment"
        "segment/next" | "segment"
        "/segment"     | "segment"
    }


    def "tail returns the segments of the path after the first"() {
        expect:
        Paths.tail(input) == expected

        where:
        input                  | expected
        ""                     | ""
        "/"                    | ""
        "/segment"             | ""
        "segment"              | ""
        "/segment/next"        | "next"
        "segment/next"         | "next"
        "segment/next/onemore" | "next/onemore"
    }


    def "parent returns the segments of the path before the last segment"() {
        expect:
        Paths.parent(input) == expected

        where:
        input                  | expected
        ""                     | ""
        "/"                    | ""
        "/segment"             | "/"
        "segment"              | ""
        "/segment/next"        | "/segment"
        "segment/next"         | "segment"
        "segment/next/onemore" | "segment/next"
    }


    def "basename returns the last segment of the path"() {
        expect:
        Paths.basename(input) == expected

        where:
        input                  | expected
        ""                     | ""
        "/"                    | ""
        "/segment"             | "segment"
        "segment"              | "segment"
        "/segment/next"        | "next"
        "segment/next"         | "next"
        "segment/next/onemore" | "onemore"
    }


    def "ancestorOf returns true if the first path is an ancestor of the second path"() {
        expect:
        Paths.ancestorOf(first, second) == expected

        where:
        first      | second         | expected
        ""         | ""             | false
        ""         | "/"            | false
        "/"        | "/segment"     | true
        "/segment" | "/segment"     | false
        "segment"  | "segment/next" | true
        "a/b/c"    | "a/b/c/d/e/f"  | true
    }


    def "selfOrAncestorOf returns true if the first path is or is an ancestor of the second path"() {
        expect:
        Paths.selfOrAncestorOf(first, second) == expected

        where:
        first      | second         | expected
        ""         | ""             | false
        ""         | "/"            | false
        "/"        | "/segment"     | true
        "/segment" | "/segment"     | true
        "segment"  | "segment"      | true
        "segment"  | "segment/next" | true
        "a/b/c"    | "a/b/c/d/e/f"  | true
    }


    def "depth returns the number of segments in the path"() {
        expect:
        Paths.depth(input) == expected

        where:
        input           | expected
        ""              | 0
        "/"             | 0
        "/segment"      | 1
        "segment"       | 1
        "/segment/next" | 2
        "segment/next"  | 2
    }


    def "isAbsolute returns true if the path begins with a /"() {
        expect:
        Paths.isAbsolute(input) == expected

        where:
        input           | expected
        ""              | false
        "/"             | true
        "/segment"      | true
        "segment"       | false
        "/segment/next" | true
        "segment/next"  | false
    }


    def "isRoot returns true if the path is /"() {
        expect:
        Paths.isRoot(input) == expected

        where:
        input           | expected
        ""              | false
        "/"             | true
        "/segment"      | false
        "/segment/next" | false
    }


    def "resolve joins paths"() {
        expect:
        Paths.resolve(first, second) == expected

        where:
        first      | second    | expected
        ""         | "segment" | "segment"
        "/"        | "segment" | "/segment"
        "/segment" | "next"    | "/segment/next"
        "/segment" | "/next"   | "/next"
    }
}

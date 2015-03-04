package com.twcable.jackalope.impl.jcr

import spock.lang.Specification
import spock.lang.Subject

@Subject(RangeIteratorImpl)
class RangeIteratorImplSpec extends Specification {

    def "skip skips elements in the iterator"() {
        def iterator = new RangeIteratorImpl<>(["a", "b", "c", "d", "e", "f", "g"])

        when:
        iterator.skip(1)

        then:
        iterator.next() == "b"

        when:
        iterator.skip(3)

        then:
        iterator.next() == "f"
    }


    def "skip throws NoSuchElementException if skipped past the last element in the iterator."() {
        when:
        def iterator = new RangeIteratorImpl<String>(["a", "b", "c"]);
        iterator.next() // "a"
        iterator.skip(3)

        then:
        thrown(NoSuchElementException)

        when:
        def emptyIterator = new RangeIteratorImpl<String>([]);
        emptyIterator.skip(1)

        then:
        thrown(NoSuchElementException)
    }


    def "the iterator keeps track of its position"() {
        when:
        def iterator = new RangeIteratorImpl<>(["a", "b", "c", "d", "e", "f", "g"])

        then:
        iterator.position == 0

        when:
        iterator.skip(1)

        then:
        iterator.position == 1

        when:
        iterator.next() // "b"

        then:
        iterator.position == 2

        when:
        iterator.skip(3)

        then:
        iterator.position == 5

        when:
        iterator.next() // "f"

        then:
        iterator.position == 6

        when:
        iterator.next()

        then:
        iterator.position == 7
    }

}

package com.twcable.jackalope.impl.jcr

import spock.lang.Specification
import spock.lang.Subject

import javax.jcr.Credentials

@Subject(RepositoryImpl)
class RepositoryImplSpec extends Specification {

    def "Our repository has a single shared session for testing"() {
        def repository = new RepositoryImpl();

        expect:
        repository.login() == repository.login("test")
        repository.login() == repository.login(new Credentials() {})
        repository.login() == repository.login(new Credentials() {}, "test")
    }

}

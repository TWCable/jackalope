package com.twcable.jackalope.impl.sling;

/**
 * A RuntimeException that wraps checked exceptions
 */
@SuppressWarnings("UnusedDeclaration")
public class SlingRepositoryException extends RuntimeException {
    public SlingRepositoryException() {
        super();
    }


    public SlingRepositoryException(String message) {
        super(message);
    }


    public SlingRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }


    public SlingRepositoryException(Throwable cause) {
        super(cause);
    }
}

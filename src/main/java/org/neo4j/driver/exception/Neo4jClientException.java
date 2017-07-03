package org.neo4j.driver.exception;

/**
 * Main exception class for this project.
 */
public class Neo4jClientException extends RuntimeException {

    /**
     * Constructor that wraps the given exception.
     *
     * @param e The underlying exception.
     */
    public Neo4jClientException(Exception e) {
        super(e.getMessage(), e.getCause());
    }

    /**
     * Constructor with only a message.
     *
     * @param message Message of the exception
     */
    public Neo4jClientException(String message) {
        super(message);
    }

}

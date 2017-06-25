package org.neo4j.driver;

public class Neo4jClientException extends RuntimeException {

    public Neo4jClientException(Exception e) {
        super(e.getMessage(), e.getCause());
    }

    public Neo4jClientException(String message) {
        super(message);
    }

}

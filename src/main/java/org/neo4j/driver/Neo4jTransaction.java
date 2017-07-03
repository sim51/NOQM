package org.neo4j.driver;

import org.neo4j.driver.exception.Neo4jClientException;
import org.neo4j.driver.v1.*;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliterator;

/**
 * Client transaction.
 */
public class Neo4jTransaction implements AutoCloseable {

    /**
     * Neo4j's underlying session.
     */
    private Session session;

    /**
     * Neo4j's underlying transaction
     */
    private Transaction transaction;

    /**
     * Default constructor with a session.
     */
    public Neo4jTransaction(Session session) {
        this.session = session;
        this.transaction = session.beginTransaction();
    }

    /**
     * Execute the given cypher query with its parameters.
     *
     * @param query  Cypher query
     * @param params Query's parameters
     * @return A Stream of record
     */
    public Stream<Record> run(String query, Value params) {
        checkSessionAndTransaction();
        try {
            StatementResult result = this.transaction.run(query, params);
            return StreamSupport.stream(spliterator(result, Long.MAX_VALUE, Spliterator.ORDERED), false);
        } catch (Exception e) {
            throw new Neo4jClientException(e);
        }
    }

    /**
     * Execute the given cypher query without parameters.
     *
     * @param query Cypher query
     * @return A Stream of record
     */
    public Stream<Record> run(String query) {
        checkSessionAndTransaction();
        return this.run(query, null);
    }

    /**
     * Commit and close the current transaction.
     */
    public void success() {
        checkSessionAndTransaction();
        this.transaction.success();
        this.transaction.close();
    }

    /**
     * Rollback and close the current transaction.
     */
    public void failure() {
        checkSessionAndTransaction();
        this.transaction.failure();
        this.transaction.close();
    }

    /**
     * Get the last bookmarkId.
     * It's only works when the underline transaction has been close.
     * So you have to call {@link #failure()} or {@link #success()} before.
     *
     * @return the latest bookmarkId of this session.
     */
    public String getBookmarkId() {
        return this.session.lastBookmark();
    }

    @Override public void close() {
        if (this.transaction != null)
            this.transaction.close();
        if (this.session != null)
            this.session.close();
    }

    /**
     * Check if one the underlying session or transaction is closed or not.
     * If so, a {@link Neo4jClientException} is thrown.
     */
    private void checkSessionAndTransaction() throws Neo4jClientException {
        if (this.session == null || !this.session.isOpen() || this.transaction == null || !this.transaction.isOpen()) {
            throw new Neo4jClientException("Session or transaction is closed");
        }
    }

}

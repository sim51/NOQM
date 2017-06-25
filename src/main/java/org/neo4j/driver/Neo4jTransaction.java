package org.neo4j.driver;

import org.neo4j.driver.v1.*;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Spliterators.spliterator;

/**
 * Wrapper on Neo4j Transaction.
 */
public class Neo4jTransaction implements AutoCloseable {

    private Session     session;
    private Transaction transaction;

    public Neo4jTransaction(Session session) {
        this.session = session;
        this.transaction = session.beginTransaction();
    }

    private void checkSessionAndTransaction() throws RuntimeException {
        if (this.session == null || !this.session.isOpen() || this.transaction == null || !this.transaction.isOpen()) {
            throw new Neo4jClientException("Session or transaction is closed");
        }
    }

    public Stream<Record> run(String query, Value params) throws RuntimeException {
        checkSessionAndTransaction();
        UncheckedCloseable close = null;
        try {
            StatementResult result = this.transaction.run(query, params);
            return StreamSupport.stream(spliterator(result, Long.MAX_VALUE, Spliterator.ORDERED), false);
        } catch (Exception e) {
            throw new Neo4jClientException(e);
        }
    }

    public Stream<Record> run(String query) throws RuntimeException {
        checkSessionAndTransaction();
        return this.run(query, null);
    }

    public void success() throws RuntimeException {
        checkSessionAndTransaction();
        this.transaction.success();
    }

    public void failure() throws RuntimeException {
        checkSessionAndTransaction();
        this.transaction.failure();
    }

    public String getBookmarkId() {
        return this.session.lastBookmark();
    }

    @Override
    public void close() {
        this.transaction.close();
        this.session.close();
    }

}

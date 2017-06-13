package org.neo4j.driver;

import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Value;

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
            throw new RuntimeException("Session or transaction is closed");
        }
    }

    public StatementResult run(String query, Value params) throws RuntimeException {
        checkSessionAndTransaction();
        return this.transaction.run(query, params);
    }

    public StatementResult run(String query) throws RuntimeException {
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
        if(this.transaction != null) {
            this.transaction.close();
            this.transaction = null;
        }
        if(this.session != null) {
            this.session.close();
            this.session = null;
        }
    }

}

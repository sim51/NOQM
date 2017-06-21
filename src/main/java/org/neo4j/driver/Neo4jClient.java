package org.neo4j.driver;

import org.neo4j.driver.v1.*;

public class Neo4jClient {

    /**
     * Singleton instance of the client.
     */
    private static Neo4jClient client;

    /**
     * Neo4j driver instance.
     */
    private Driver driver;

    /**
     * Retrieve the singleton instance.
     *
     * @return The Neo4jClient
     */
    private static synchronized Neo4jClient getInstance(){
        if (client == null) {
            try {
                client = new Neo4jClient();
            } catch (Exception e) {
                // just cast the exception to a runtime,
                // to avoid to have a signature error on this method.
                throw new RuntimeException(e);
            }
        }

        return client;
    }

    /**
     * Constructor of Neo4j client.
     * It just create a Neo4j driver instance with the help of the <code>neo4j-driver.properties</code> file.
     */
    private Neo4jClient() throws Exception {
        // Load the configuration
        Configuration config = new Configuration();

        // Create the Neo4j driver instance
        this.driver = GraphDatabase.driver(
                config.getStringOrDefault("neo4j.url", "bolt://localhost"),
                AuthTokens.basic(
                        config.getStringOrDefault("neo4j.user", "neo4j"),
                        config.getStringOrDefault("neo4j.password", "neo4j")
                ),
                config.toDriverConfig()
        );
    }

    @Override
    public void finalize() throws Throwable {
        if (this.driver != null) {
            driver.close();
        }
    }


    /*-------------------------------*/
	/*       Auto Commit mode        */
	/*-------------------------------*/

    /**
     * Execute a read cypher query with parameters to Neo4j.
s     */
    private static StatementResult run(String query, Value parameters, AccessMode mode, String bookmarkId) {
        StatementResult rs = null;
        try (Session session = getInstance().driver.session(mode, bookmarkId)) {
            rs = session.run(query, parameters);
            // Start to consume the result to avoid lazy exception
            rs.hasNext();
        }
        return rs;
    }

    /**
     * Execute a read cypher query to Neo4j.
     */
    public static StatementResult read(String query) {
        return run(query, Values.EmptyMap, AccessMode.READ, null);
    }

    /**
     * Execute a read cypher query to Neo4j with a bookmarkId.
     */
    public static StatementResult read(String query, String bookmarkId) {
        return run(query, Values.EmptyMap, AccessMode.READ, bookmarkId);
    }

    /**
     * Execute a read cypher query to Neo4j with parameters.
     */
    public static StatementResult read(String query, Value parameters) {
        return run(query, parameters, AccessMode.READ, null);
    }

    /**
     * Execute a read cypher query to Neo4j with parameters and bookmarkId.
     */
    public static StatementResult read(String query, Value parameters, String bookmarkId) {
        return run(query, parameters, AccessMode.READ, bookmarkId);
    }

    /**
     * Execute a write cypher query to Neo4j.
     */
    public static StatementResult write(String query) {
        return run(query, Values.EmptyMap, AccessMode.WRITE, null);
    }

    /**
     * Execute a write cypher query to Neo4j with a bookmarkId.
     */
    public static StatementResult write(String query, String bookmarkId) {
        return run(query, Values.EmptyMap, AccessMode.WRITE, bookmarkId);
    }

    /**
     * Execute a write cypher query to Neo4j with parameters.
     */
    public static StatementResult write(String query, Value parameters) {
        return run(query, parameters, AccessMode.WRITE, null);
    }

    /**
     * Execute a write cypher query to Neo4j with parameters and bookmarkId.
     */
    public static StatementResult write(String query, Value parameters, String bookmarkId) {
        return run(query, parameters, AccessMode.WRITE, bookmarkId);
    }


    /*-------------------------------*/
	/*       Transaction mode        */
	/*-------------------------------*/

    /**
     * Retrieve a Transaction session.
     */
    private static Neo4jTransaction getTransaction(AccessMode mode, String bookmarkId) {
        return new Neo4jTransaction(getInstance().driver.session(mode, bookmarkId));
    }

    public static Neo4jTransaction getReadTransaction() {
        return getTransaction(AccessMode.READ, null);
    }

    public static Neo4jTransaction getReadTransaction(String bookmarkId) {
        return getTransaction(AccessMode.READ, bookmarkId);
    }

    public static Neo4jTransaction getWriteTransaction() {
        return getTransaction(AccessMode.WRITE, null);
    }

    public static Neo4jTransaction getWriteTransaction(String bookmarkId) {
        return getTransaction(AccessMode.WRITE, bookmarkId);
    }

}

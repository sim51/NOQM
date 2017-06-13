package org.neo4j.driver;

import org.neo4j.driver.v1.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
        Map<String, Object> config = getConfiguration();
        this.driver = GraphDatabase.driver((String) config.getOrDefault("neo4j.url", "bolt://localhost"), AuthTokens.basic((String) config.getOrDefault("neo4j.user", "neo4j"), (String) config.getOrDefault("neo4j.password", "neo4j")));
    }

    private Map<String, Object> getConfiguration() throws Exception {
        Map<String, Object> config = new HashMap<>();

        // Loading neo4j-driver.properties
        Properties prop = new Properties();
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("neo4j-driver.properties");
        try {
            prop.load(stream);
            for (final String name: prop.stringPropertyNames()) {
                config.put(name, prop.getProperty(name));
            }
        } catch (IOException e) {
            throw new Exception("File neo4j-driver.properties not found !!!");
        }

        // Loading neo4j-driver-ext.properties to override default config
        Properties propExt = new Properties();
        InputStream streamExt = this.getClass().getClassLoader().getResourceAsStream("neo4j-driver-ext.properties");
        try {
            propExt.load(streamExt);
            for (final String name: propExt.stringPropertyNames()) {
                config.put(name, propExt.getProperty(name));
            }
        } catch (Exception e) {
            // silent exception, we don't care
        }
        return config;
    }

    private Config mapToDriverConfig(Map<String, Object> props) {
        Config.ConfigBuilder cb = Config.build();

        if( props.containsKey("neo4j.maxIdleConnectionPoolSize") ) {
            cb.withMaxIdleSessions((Integer) props.get("neo4j.maxIdleConnectionPoolSize"));
        }

        if( props.containsKey("neo4j.idleTimeBeforeConnectionTest") ) {
            cb.withConnectionLivenessCheckTimeout((Long) props.get("neo4j.idleTimeBeforeConnectionTest"), TimeUnit.SECONDS);
        }

        if( props.containsKey("neo4j.logLeakedSessions") ) {
            if((Boolean) props.get("neo4j.logLeakedSessions")) {
                cb.withLeakedSessionsLogging();
            }
        }

        if( props.containsKey("neo4j.encrypted") ) {
            if((Boolean) props.get("neo4j.encrypted")) {
                cb.withEncryption();
            }
        }

        //TODO:
        if( props.containsKey("neo4j.trustStrategy") ) {
        }
        if( props.containsKey("neo4j.routingFailureLimit") ) {
        }
        if( props.containsKey("neo4j.routingRetryDelayMillis") ) {
        }
        if( props.containsKey("neo4j.connectionTimeoutMillis") ) {
        }
        if( props.containsKey("neo4j.retrySettings") ) {
        }

        return cb.toConfig();
    }

    /**
     * Execute a read cypher query with parameters to Neo4j.
     *
     * @param mode
     * @param query
     * @param parameters
     * @return
     */
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
     *
     * @param query
     * @return
     */
    public static StatementResult read(String query) {
        return run(query, Values.EmptyMap, AccessMode.READ, null);
    }

    /**
     * Execute a read cypher query to Neo4j with a bookmarkId.
     *
     * @param query
     * @return
     */
    public static StatementResult read(String query, String bookmarkId) {
        return run(query, Values.EmptyMap, AccessMode.READ, bookmarkId);
    }

    /**
     * Execute a read cypher query to Neo4j with parameters.
     *
     * @param query
     * @return
     */
    public static StatementResult read(String query, Value parameters) {
        return run(query, parameters, AccessMode.READ, null);
    }

    /**
     * Execute a read cypher query to Neo4j with parameters and bookmarkId.
     *
     * @param query
     * @return
     */
    public static StatementResult read(String query, Value parameters, String bookmarkId) {
        return run(query, parameters, AccessMode.READ, bookmarkId);
    }

    /**
     * Execute a write cypher query to Neo4j.
     *
     * @param query
     * @return
     */
    public static StatementResult write(String query) {
        return run(query, Values.EmptyMap, AccessMode.WRITE, null);
    }

    /**
     * Execute a write cypher query to Neo4j with a bookmarkId.
     *
     * @param query
     * @return
     */
    public static StatementResult write(String query, String bookmarkId) {
        return run(query, Values.EmptyMap, AccessMode.WRITE, bookmarkId);
    }

    /**
     * Execute a write cypher query to Neo4j with parameters.
     *
     * @param query
     * @return
     */
    public static StatementResult write(String query, Value parameters) {
        return run(query, parameters, AccessMode.WRITE, null);
    }

    /**
     * Execute a write cypher query to Neo4j with parameters and bookmarkId.
     *
     * @param query
     * @return
     */
    public static StatementResult write(String query, Value parameters, String bookmarkId) {
        return run(query, parameters, AccessMode.WRITE, bookmarkId);
    }

    /**
     * Retrieve a Transaction session.
     *
     * @return
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

    @Override public void finalize() throws Throwable {
        if (this.driver != null) {
            driver.close();
        }
    }
}

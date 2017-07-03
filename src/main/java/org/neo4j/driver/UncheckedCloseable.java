package org.neo4j.driver;

import org.neo4j.driver.exception.Neo4jClientException;

/**
 * Wrap an AutoCloseable object for adding the Runnable trait.
 */
public interface UncheckedCloseable extends Runnable, AutoCloseable {

    /**
     * Wrap the defined AutoCloseable object.
     */
    static UncheckedCloseable wrap(AutoCloseable c) {
        return c::close;
    }

    /**
     * Default implementation of {@link Runnable#run}.
     */
    default void run() {
        try {
            close();
        } catch (Exception e) {
            throw new Neo4jClientException(e);
        }
    }
}

package org.neo4j.driver;

public interface UncheckedCloseable extends Runnable, AutoCloseable {

    default void run() {
        try {
            close();
        } catch (Exception e) {
            throw new Neo4jClientException(e);
        }
    }

    default UncheckedCloseable nest(AutoCloseable c) {
        return () -> {
            try (UncheckedCloseable c1 = this) {
                c.close();
            }
        };
    }

    static UncheckedCloseable wrap(AutoCloseable c) {
        return c::close;
    }
}

package org.neo4j.driver.projection;

import org.neo4j.driver.exception.Neo4jClientException;
import org.neo4j.driver.v1.Record;

import java.util.function.Function;

/**
 * Abstract class for projection.
 * A projection is just a function that can be passed to a map.
 */
abstract class Projection<T> implements Function<Record, T> {

    /**
     * Projection's type.
     */
    protected Class type;

    /**
     * Default constructor.
     *
     * @param type
     */
    public Projection(Class type) {
        this.type = type;
    }

    /**
     * Check if the given <code>record</code> has only one column.
     * if not, a {@link Neo4jClientException} is throw.
     *
     * @param record
     */
    protected void checkIfRecordHaveSingleValue(Record record) {
        if (record.size() > 1) {
            throw new Neo4jClientException("Record doesn't have a single column -> can't cast it to primitive object type");
        }
    }

}

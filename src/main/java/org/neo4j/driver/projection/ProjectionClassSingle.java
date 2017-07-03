package org.neo4j.driver.projection;

import org.neo4j.driver.v1.Record;

/**
 * Projection for simple type and on single column result.
 */
public class ProjectionClassSingle<T> extends Projection<T> {

    /**
     * Constructor.
     */
    public ProjectionClassSingle(Class type) {
        super(type);
    }

    @Override public T apply(Record record) {
        checkIfRecordHaveSingleValue(record);
        return (T) ConversionHelper.convertDriverValueTo(type, record.get(0));
    }

}

package org.neo4j.driver.projection;

import org.neo4j.driver.Neo4jClientException;
import org.neo4j.driver.v1.Record;

public class ProjectionPrimitiveTypeSingle<T> extends ProjectionPrimitiveType<T> {

    public ProjectionPrimitiveTypeSingle(Class type) {
        super(type);
    }

    @Override
    public T apply(Record record) {
        checkIfRecordHaveSingleValue(record);
        return super.apply(record);
    }

    private void checkIfRecordHaveSingleValue(Record record) {
        if(record.size() > 1) {
            throw new Neo4jClientException("Record doesn't have a single column -> can't cast it to primitive object type");
        }
    }
}

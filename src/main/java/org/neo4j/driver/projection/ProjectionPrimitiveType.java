package org.neo4j.driver.projection;

import org.neo4j.driver.v1.Record;

import java.util.function.Function;

public class ProjectionPrimitiveType<T> extends Projection<T> implements Function<Record, T> {


    public ProjectionPrimitiveType(Class type) {
        super(type);
    }

    @Override
    public T apply(Record record) {
        return (T) this.convertDriverValue(type, record.get(0));
    }
}

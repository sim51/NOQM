package org.neo4j.driver.projection;

import org.neo4j.driver.v1.Record;

/**
 * Projection for Pojo.
 */
public class ProjectionClass<T> extends Projection<T> {

    /**
     * Default constructor.
     */
    public ProjectionClass(Class type) {
        super(type);
    }

    @Override public T apply(Record record) {
        DynamicClassConstructor<T> generator = new DynamicClassConstructor(type);
        T result = generator.construct();
        for (String key : record.keys()) {
            generator.add(result, key, record.get(key));
        }
        return result;
    }

}

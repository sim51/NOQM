package org.neo4j.driver.projection;

import org.neo4j.driver.v1.Record;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Projection for list type on single column result.
 */
public class ProjectionListSingle extends Projection<List> {

    /**
     * Type of list's object.
     */
    protected Type listType;

    /**
     * Default constructor.
     */
    public ProjectionListSingle(Type type) {
        super(ArrayList.class);
        listType = type;
    }

    @Override public List apply(Record record) {
        checkIfRecordHaveSingleValue(record);
        return (List) ConversionHelper.convertDriverValueTo(listType, record.get(0));
    }

}

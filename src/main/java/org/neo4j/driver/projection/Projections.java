package org.neo4j.driver.projection;

public class Projections {

    public static <T> Projection<T> create(Class<T> type) {

        if(ProjectionPrimitiveType.PRIMITIVE_TYPE.contains(type.getSimpleName())){
            return new ProjectionPrimitiveTypeSingle<T>(type);
        }

        return new ProjectionPojo<T>(type);
    }

}

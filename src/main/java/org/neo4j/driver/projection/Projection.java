package org.neo4j.driver.projection;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

abstract class Projection<T> implements Function<Record, T> {

    protected static final Set<String> PRIMITIVE_TYPE = new HashSet<String>(
            Arrays.asList(
                    String.class.getSimpleName(),
                    Long.class.getSimpleName(),
                    Integer.class.getSimpleName(),
                    Float.class.getSimpleName(),
                    Boolean.class.getSimpleName(),
                    Number.class.getSimpleName(),
                    Double.class.getSimpleName(),
                    Node.class.getSimpleName(),
                    Relationship.class.getSimpleName(),
                    Path.class.getSimpleName(),
                    List.class.getSimpleName()
            )
    );

    protected Class type;

    public Projection(Class type) {
        this.type = type;
    }

    protected <Z>Z convertDriverValue(Class<Z> clazz, Value value) {
        Z result = null;

        // Depends of the projection
        switch (clazz.getSimpleName()) {
            case "String":
                if (value != null) {
                    result = (Z) value.toString();
                }
                break;
            case "Long":
                if (value != null) {
                    result = (Z) Long.valueOf(value.asLong());
                }
                break;
            case "Integer":
                if (value != null) {
                    result = (Z) Integer.valueOf(value.asInt());
                }
                break;
            case "Float":
                if (value != null) {
                    result = (Z) Float.valueOf(value.asFloat());
                }
                break;
            case "Boolean":
                if (value != null) {
                    result = (Z) Boolean.valueOf(value.asBoolean());
                }
                break;
            case "Number":
                if (value != null) {
                    result = (Z) value.asNumber();
                }
                break;
            case "Double":
                if (value != null) {
                    result = (Z) Double.valueOf(value.asDouble());
                }
                break;
            case "Node":
                if (value != null) {
                    result = (Z) value.asNode();
                }
                break;
            case "Relationship":
                if (value != null) {
                    result = (Z) value.asRelationship();
                }
                break;
            case "Path":
                if (value != null) {
                    result = (Z) value.asPath();
                }
                break;
            case "List":
                if(value != null) {
                    result = (Z) value.asList();
                }
        }
        return result;
    }

}

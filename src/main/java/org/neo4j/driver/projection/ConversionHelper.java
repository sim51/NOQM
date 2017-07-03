package org.neo4j.driver.projection;

import org.neo4j.driver.exception.Neo4jClientException;
import org.neo4j.driver.internal.types.InternalTypeSystem;
import org.neo4j.driver.internal.value.IntegerValue;
import org.neo4j.driver.internal.value.ListValue;
import org.neo4j.driver.internal.value.StringValue;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import java.lang.reflect.ParameterizedType;
import java.util.stream.StreamSupport;

/**
 * Helper class to easy convert Neo4j driver Value.
 */
public class ConversionHelper {

    /**
     * Convert a Neo4j driver value to the specified class.
     */
    public static <Z> Z convertDriverValueTo(Z type, Value value) {
        Z result = null;

        Class clazz;
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            clazz = (Class) ptype.getRawType();
        } else {
            clazz = (Class) type;
        }

        if (value.type() != InternalTypeSystem.TYPE_SYSTEM.NULL()) {

            // Depends of the projection
            switch (clazz.getSimpleName()) {
                case "String":
                    result = (Z) value.asString();
                    break;
                case "Long":
                    result = (Z) Long.valueOf(value.asLong());
                    break;
                case "Integer":
                    result = (Z) Integer.valueOf(value.asInt());
                    break;
                case "Float":
                    result = (Z) Float.valueOf(value.asFloat());
                    break;
                case "Boolean":
                    result = (Z) Boolean.valueOf(value.asBoolean());
                    break;
                case "Number":
                    result = (Z) value.asNumber();
                    break;
                case "Double":
                    result = (Z) Double.valueOf(value.asDouble());
                    break;
                case "Node":
                    result = (Z) value.asNode();
                    break;
                case "Relationship":
                    result = (Z) value.asRelationship();
                    break;
                case "Path":
                    // TODO : make a generic type for path ? A list of Map ?
                    result = (Z) value.asPath();
                    break;
                case "ArrayList":
                case "List":
                    if (type instanceof ParameterizedType && ((ParameterizedType) type).getActualTypeArguments().length == 1) {
                        try {
                            Class pClazz = Class.forName(((ParameterizedType) type).getActualTypeArguments()[0].getTypeName());
                            result = (Z) value.asList(e -> pClazz.cast(convertDriverValueTo(pClazz, e)));
                        } catch (ClassNotFoundException e) {
                            throw new Neo4jClientException(e);
                        }
                    } else {
                        result = (Z) value.asList();
                    }
                    break;
                case "Map":
                    result = (Z) value.asMap();
                    break;
                case "byte[]":
                    result = (Z) value.asByteArray();
                    break;
                // projection type is a more complex
                default:
                    if (InternalTypeSystem.TYPE_SYSTEM.NODE().isTypeOf(value)) {
                        result = (Z) convertDriverNodeTo(clazz, value.asNode());
                    }
                    if (InternalTypeSystem.TYPE_SYSTEM.RELATIONSHIP().isTypeOf(value)) {
                        result = (Z) convertDriverRelationshipTo(clazz, value.asRelationship());
                    }
                    break;
            }

        }

        return result;
    }

    /**
     * Convert a Neo4j driver Node to the specified class.
     */
    private static <Z> Z convertDriverNodeTo(Class<Z> clazz, Node node) {
        Z result = null;
        if (node != null) {
            DynamicClassConstructor<Z> generator = new DynamicClassConstructor(clazz);
            result = generator.construct();

            // Try to set the neo4j internal id
            generator.add(result, "id", new IntegerValue(node.id()));

            // Try to set Neo4j labels
            StringValue[] labels = StreamSupport.stream(node.labels().spliterator(), false).map(item -> new StringValue(item)).toArray(StringValue[]::new);

            generator.add(result, "labels", new ListValue(labels));

            // Setting all node's properties
            for (String key : node.keys()) {
                generator.add(result, key, node.get(key));
            }
        }
        return result;
    }

    /**
     * Convert a Neo4j driver Relationship to the specified class.
     */
    private static <Z> Z convertDriverRelationshipTo(Class<Z> clazz, Relationship rel) {
        Z result = null;
        if (rel != null) {
            DynamicClassConstructor<Z> generator = new DynamicClassConstructor(clazz);
            result = generator.construct();

            // Try to set the Neo4j internal id
            generator.add(result, "id", new IntegerValue(rel.id()));

            // Try to set Neo4j type
            generator.add(result, "type", new StringValue(rel.type()));

            // Try to set Neo4j start node
            generator.add(result, "startId", new IntegerValue(rel.startNodeId()));

            // Try to set Neo4j end node
            generator.add(result, "endId", new IntegerValue(rel.endNodeId()));

            // Setting all node's properties
            for (String key : rel.keys()) {
                generator.add(result, key, rel.get(key));
            }
        }
        return result;
    }
}

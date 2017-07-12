package org.neo4j.driver.projection;

import org.neo4j.driver.exception.Neo4jClientException;
import org.neo4j.driver.v1.Value;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that helps to dynamically construct object.
 */
public class DynamicClassConstructor<T> {

    /**
     * Type to construct.
     */
    protected Class type;

    /**
     * List of setters of the class {@link #type}
     */
    private Map<String, Method> setters = new HashMap<>();

    /**
     * Constructor that initialize the {@link #type} and compute class setter.
     *
     * @param type : the type to construct.
     */
    public DynamicClassConstructor(Class type) {
        this.type = type;

        // Find setters of class
        Method[] methods = type.getDeclaredMethods();
        for (Method method : methods) {
            if (this.isSetter(method)) {
                String attributName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
                this.setters.put(attributName, method);
            }
        }
    }

    /**
     * Create a new instance object of type {@link #type}.
     */
    public T construct() {
        try {
            return (T) type.newInstance();
        } catch (Exception e) {
            throw new Neo4jClientException(e);
        }
    }

    /**
     * Add a property to an object by it's name.
     *
     * @param obj   Object to enhanced
     * @param key   Parameter name
     * @param value Value of the parameter
     */
    public void add(T obj, String key, Value value) {
        try {
            if (this.setters.containsKey(key)) {
                Method setter = this.setters.get(key);
                Type pType = setter.getGenericParameterTypes()[0];
                Object prop = ConversionHelper.convertDriverValueTo(pType, value);
                if (value != null) {
                    setter.invoke(obj, prop);
                }

            }
        } catch (Exception e) {
            throw new Neo4jClientException(e);
        }
    }

    /**
     * Determinate if the specified method is a setter.
     * A Setter is a method :
     * - public
     * - no return type
     * - only one parameter
     * - name that stats with 'set'
     *
     * @param method The method to check
     * @return Return <code>TRUE</code> if the specified method is a Setter, <code>FALSE</code> otherwise.
     */
    private boolean isSetter(Method method) {
        return Modifier.isPublic(method.getModifiers()) && method.getReturnType().equals(void.class) && method.getParameterTypes().length == 1 && method
                .getName().matches("^set[A-Z_].*");
    }
}

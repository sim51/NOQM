package org.neo4j.driver.projection;

import org.neo4j.driver.Neo4jClientException;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ProjectionPojo<T> extends Projection<T> implements Function<Record, T> {

    private Map<String, Method> setters = new HashMap<>();

    public ProjectionPojo(Class type) {
        super(type);

        // Find setters of class
        ArrayList<Method> list = new ArrayList<Method>();
        Method[] methods = type.getDeclaredMethods();
        for (Method method : methods) {
            if (this.isSetter(method)) {
                String attibutName = method.getName().substring(3,4).toLowerCase() + method.getName().substring(4);
                this.setters.put(attibutName, method);
            }
        }
    }

    @Override
    public T apply(Record record) {
        try {
            Boolean hasSettingValue = Boolean.FALSE;

            T result = (T) type.newInstance();

            for(String key: record.keys()) {
                if (this.setters.containsKey(key)) {
                    Method setter = this.setters.get(key);
                    Class typeParameter = setter.getParameterTypes()[0];
                    Object value = this.convertDriverValue(typeParameter, record.get(key));
                    if(value != null ){
                        setter.invoke(result, value);
                        hasSettingValue = Boolean.TRUE;
                    }
                }
            }

            if(!hasSettingValue) {
                result = null;
            }

            return result;
        } catch (Exception e) {
            throw new Neo4jClientException(e);
        }
    }

    private boolean isSetter(Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
                method.getReturnType().equals(void.class) &&
                method.getParameterTypes().length == 1 &&
                method.getName().matches("^set[A-Z].*");
    }

}

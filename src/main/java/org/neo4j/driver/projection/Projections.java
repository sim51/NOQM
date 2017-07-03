package org.neo4j.driver.projection;

/**
 * Factory that generate {@link Projection}.
 */
public class Projections {

    /**
     * Create a <code>type</code> projection for a query result with only one column.
     */
    public static <T> Projection<T> singleAs(Class<T> type) {
        return new ProjectionClassSingle<T>(type);
    }

    /**
     * Create a <code>type</code> projection for a query result with only one column that is a list.
     * This was created because a list of class is not a class.
     * SO to call this method you have to pass the parameter like this <code>new ArrayList<Movie>(){}.getClass()</code>.
     */
    public static ProjectionListSingle singleAsListOf(Class type) {
        return new ProjectionListSingle(type.getGenericSuperclass());
    }

    /**
     * Create a simple <code>type</code> projection.
     */
    public static <T> Projection<T> as(Class<T> type) {
        return new ProjectionClass<T>(type);
    }

}

package org.neo4j.driver.projection.pojo;

import lombok.Data;

@Data
public class MovieActorCouple {

    public Person actor;
    public ActedIn actedIn;
    public Movie movie;
}

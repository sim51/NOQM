package org.neo4j.driver.projection.pojo;

import lombok.Data;

import java.util.List;

@Data
public class CastingMovie {

    public Movie        movie;
    public List<Person> actors;
}

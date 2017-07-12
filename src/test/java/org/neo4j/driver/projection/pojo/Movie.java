package org.neo4j.driver.projection.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Movie {

    public Long         id;
    public List<String> labels;
    public String       title;
    public String       tagline;
    public Integer      released;

}

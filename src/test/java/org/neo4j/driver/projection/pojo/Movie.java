package org.neo4j.driver.projection.pojo;

import lombok.Data;

@Data
public class Movie {

    public String title;
    public String tagline;
    public Integer released;

}

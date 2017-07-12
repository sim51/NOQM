package org.neo4j.driver.projection.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ActedIn {

    public Long         _id;
    public List<String> roles;
}

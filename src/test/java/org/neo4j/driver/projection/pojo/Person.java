package org.neo4j.driver.projection.pojo;

import lombok.Data;

import java.util.List;

@Data public class Person {

    public Long         id;
    public List<String> labels;
    public String       name;
    public Integer      born;
}

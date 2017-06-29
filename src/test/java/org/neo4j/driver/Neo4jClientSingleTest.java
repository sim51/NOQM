package org.neo4j.driver;

import org.neo4j.driver.junit.CustomNeo4jRule;

public class Neo4jClientSingleTest extends AbstractNeo4jClientTest {

    public Neo4jClientSingleTest() {
        super(new CustomNeo4jRule(CustomNeo4jRule.Mode.SINGLE));
    }
}

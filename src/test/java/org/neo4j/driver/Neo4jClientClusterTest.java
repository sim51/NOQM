package org.neo4j.driver;

import org.neo4j.driver.junit.CustomNeo4jRule;

public class Neo4jClientClusterTest extends AbstractNeo4jClientTest {

    public Neo4jClientClusterTest() {
        super(new CustomNeo4jRule(CustomNeo4jRule.Mode.CLUSTER));
    }
}

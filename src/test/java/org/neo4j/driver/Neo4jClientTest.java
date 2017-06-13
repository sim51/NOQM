package org.neo4j.driver;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.harness.junit.Neo4jRule;

import java.io.File;

public class Neo4jClientTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withConfig("dbms.connector.bolt.listen_address", ":5657")
            .withFixture(new File("./src/test/resources/cypher/movie.cyp"));

    @Test
    public void retrieve_read_result_should_succeed(){
        StatementResult rs = Neo4jClient.read("MATCH (n) RETURN n");
        Assert.assertEquals(169, rs.list().size());
    }

}

package org.neo4j.driver;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.driver.junit.AbstractClusterUnitTest;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import java.util.stream.Stream;

public class Neo4jClientClusterTest extends AbstractClusterUnitTest {

    @Test
    public void autocommit_read_query_result_should_succeed() {
        try(Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN n")) {
            Assert.assertEquals(169, rs.count());
        }
    }

}

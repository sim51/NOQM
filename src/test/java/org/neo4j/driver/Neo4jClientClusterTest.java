package org.neo4j.driver;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.util.cc.ClusterMember;
import org.neo4j.driver.v1.util.cc.ClusterRule;

public class Neo4jClientClusterTest {

    @ClassRule
    public static final ClusterRule clusterRule = new ClusterRule();

    @BeforeClass
    public static void before() throws Exception {
        ClusterMember node = clusterRule.getCluster().leader();
        TestHelper.initDb(node.getRoutingUri().toString());

        // Wait until a replica as data
        Boolean stop = Boolean.FALSE;
        while(!stop) {
            if(Neo4jClient.read("MATCH (n) RETURN count(*)").single().get(0).asInt() > 0) {
                stop = Boolean.TRUE;
            }
        }
    }

    @Test
    public void autocommit_read_query_result_should_succeed() {
        StatementResult rs = Neo4jClient.read("MATCH (n) RETURN n");
        Assert.assertEquals(169, rs.list().size());
    }

}

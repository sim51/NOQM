package org.neo4j.driver.junit;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.util.cc.ClusterMember;
import org.neo4j.driver.v1.util.cc.ClusterRule;


public abstract class AbstractClusterUnitTest extends AbstractUnitTest {

    @ClassRule
    public static ClusterRule neo4j = new ClusterRule();

    @BeforeClass
    public static void before() throws Exception {
        initialize(neo4j.getCluster().leader().getRoutingUri().toString());

        // Wait until all replicas as data
        for (ClusterMember member : neo4j.getCluster().readReplicas()) {
            Boolean stop = Boolean.FALSE;
            while(!stop) {
                if(checkFixtureCount(member.getBoltUri().toString())){
                    stop = Boolean.TRUE;
                }
            }
        }

    }

    private static boolean checkFixtureCount(String url) {
        Boolean result = Boolean.FALSE;

        Driver driver = GraphDatabase.driver( url, AuthTokens.basic("neo4j", "test"));
        try (Session session = driver.session(AccessMode.READ)){
            Integer count = session.run("MATCH (n) RETURN count(*)").single().get(0).asInt();
            if(count == 169) {
                result = true;
            }
        }
        driver.close();

        return result;
    }

}

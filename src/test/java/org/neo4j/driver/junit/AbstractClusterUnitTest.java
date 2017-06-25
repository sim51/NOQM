package org.neo4j.driver.junit;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.neo4j.driver.v1.util.cc.ClusterRule;

public abstract class AbstractClusterUnitTest extends AbstractUnitTest {

    @ClassRule
    public static ClusterRule neo4j = new ClusterRule();

    @BeforeClass
    public static void before() throws Exception {
        initialize(neo4j.getCluster().leader().getRoutingUri().toString());
    }

}

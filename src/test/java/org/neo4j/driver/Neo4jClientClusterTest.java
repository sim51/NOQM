package org.neo4j.driver;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.harness.junit.EnterpriseNeo4jRule;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.server.configuration.ServerSettings;

import java.io.File;

import static org.neo4j.server.ServerTestUtils.getRelativePath;
import static org.neo4j.server.ServerTestUtils.getSharedTestTemporaryFolder;

public class Neo4jClientClusterTest {

    /*@ClassRule
    public static final Neo4jRule neo4j_1 = new EnterpriseNeo4jRule()
            .withConfig( ServerSettings.certificates_directory.name(), getRelativePath( getSharedTestTemporaryFolder(), ServerSettings.certificates_directory ) )
            .withConfig("dbms.connector.bolt.listen_address", ":5001")
            .withConfig("dbms.mode", "CORE")
            .withConfig("causal_clustering.expected_core_cluster_size", "1")
            .withConfig("causal_clustering.initial_discovery_members", "localhost:5001")
            .withConfig("causal_clustering.discovery_listen_address", ":5001")
            .withConfig("causal_clustering.transaction_listen_address", ":5101")
            .withConfig("causal_clustering.raft_listen_address", ":5201");

    @ClassRule
    public static final Neo4jRule neo4j_2 = new EnterpriseNeo4jRule()
            .withConfig( ServerSettings.certificates_directory.name(), getRelativePath( getSharedTestTemporaryFolder(), ServerSettings.certificates_directory ) )
            .withConfig("dbms.connector.bolt.listen_address", ":5002")
            .withConfig("dbms.mode", "CORE")
            .withConfig("causal_clustering.expected_core_cluster_size", "2")
            .withConfig("causal_clustering.initial_discovery_members", "localhost:5001,localhost:5002")
            .withConfig("causal_clustering.discovery_listen_address", ":5002")
            .withConfig("causal_clustering.transaction_listen_address", ":5102")
            .withConfig("causal_clustering.raft_listen_address", ":5202");

    @ClassRule
    public static final Neo4jRule neo4j_3 = new EnterpriseNeo4jRule()
            .withConfig( ServerSettings.certificates_directory.name(), getRelativePath( getSharedTestTemporaryFolder(), ServerSettings.certificates_directory ) )
            .withConfig("dbms.connector.bolt.listen_address", ":5003")
            .withConfig("dbms.mode", "CORE")
            .withConfig("causal_clustering.expected_core_cluster_size", "3")
            .withConfig("causal_clustering.initial_discovery_members", "localhost:5001,localhost:5002,localhost:5003")
            .withConfig("causal_clustering.discovery_listen_address", ":5003")
            .withConfig("causal_clustering.transaction_listen_address", ":5103")
            .withConfig("causal_clustering.raft_listen_address", ":5203");

    @ClassRule
    public static final Neo4jRule neo4j_4 = new EnterpriseNeo4jRule()
            .withConfig( ServerSettings.certificates_directory.name(), getRelativePath( getSharedTestTemporaryFolder(), ServerSettings.certificates_directory ) )
            .withConfig("dbms.connector.bolt.listen_address", ":5004")
            .withConfig("dbms.mode", "READ_REPLICA")
            .withConfig("causal_clustering.expected_core_cluster_size", "4")
            .withConfig("causal_clustering.initial_discovery_members", "localhost:5001,localhost:5002,localhost:5003,localhost:5004")
            .withConfig("causal_clustering.discovery_listen_address", ":5004")
            .withConfig("causal_clustering.transaction_listen_address", ":5104")
            .withConfig("causal_clustering.raft_listen_address", ":5204")
            .withFixture(new File("./src/test/resources/cypher/movie.cyp"));

    @Test
    public void autocommit_read_query_result_should_succeed(){
        StatementResult rs = Neo4jClient.read("MATCH (n) RETURN n");
        Assert.assertEquals(169, rs.list().size());
    }*/

}

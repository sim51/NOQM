package org.neo4j.driver;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.harness.junit.Neo4jRule;

import java.io.File;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jClientTest {

    @ClassRule
    public static Neo4jRule neo4j = new Neo4jRule()
            .withConfig("dbms.connector.bolt.listen_address", ":5001")
            .withFixture(new File("./src/test/resources/cypher/movie.cyp"));

    @Test
    public void autocommit_read_query_result_should_succeed(){
        StatementResult rs = Neo4jClient.read("MATCH (n) RETURN n");
        Assert.assertEquals(169, rs.list().size());
    }

    @Test
    public void autocommit_write_query_should_succeed(){
        StatementResult rs = Neo4jClient.write("CREATE (me:Person { name:$name, born:$born }) RETURN me", parameters( "name", "Benoit", "born", 1983 ));
        Node me = rs.single().get("me").asNode();

        Assert.assertTrue(me.hasLabel("Person"));
        Assert.assertEquals("Benoit", me.get("name").asString());
        Assert.assertEquals(1983, me.get("born").asInt());

        // Reset modifications
        Neo4jClient.write("MATCH (me:Person { name:$name, born:$born }) DELETE me", parameters( "name", "Benoit", "born", 1983 ));
        this.autocommit_read_query_result_should_succeed();
    }

    @Test
    public void tx_read_with_commit_should_succeed(){
        try ( Neo4jTransaction tx = Neo4jClient.getReadTransaction() ) {
            StatementResult rs = tx.run("MATCH (actor:Person {name:$name})-[:ACTED_IN]->(movies) RETURN actor,movies", parameters( "name", "Tom Hanks" ));
            tx.success();

            Assert.assertEquals(12, rs.list().size());
        }
    }

    @Test
    public void tx_write_with_commit_should_succeed(){
        try ( Neo4jTransaction tx = Neo4jClient.getWriteTransaction() ) {
            StatementResult rs = tx.run("CREATE (me:Person { name:$name, born:$born }) RETURN me", parameters( "name", "Benoit", "born", 1983 ));
            Node me = rs.single().get("me").asNode();

            StatementResult rs2 = tx.run("CREATE (movie:Movie { title:$title }) RETURN movie", parameters( "title", "My Favorite film"));
            Node movie = rs2.single().get("movie").asNode();

            tx.run("MATCH (n), (m) WHERE id(n)=$id1 AND id(m)=$id2 CREATE (n)-[:ACTED_IN]->(m) ", parameters( "id1", me.id(), "id2", movie.id()));
            tx.success();
        }

        StatementResult rs = Neo4jClient.read("MATCH (n:Person { name:$name, born:$born })-[r:ACTED_IN]->(m:Movie { title:$title }) RETURN n,r,m", parameters( "name", "Benoit", "born", 1983, "title", "My Favorite film"));
        Assert.assertEquals(1, rs.list().size());

        // Reset modifications
        Neo4jClient.write("MATCH (n:Person { name:$name, born:$born })-[:ACTED_IN]->(m:Movie { title:$title }) DETACH DELETE n, m", parameters( "name", "Benoit", "born", 1983, "title", "My Favorite film"));
        this.autocommit_read_query_result_should_succeed();
    }

    @Test
    public void tx_write_with_rollback_should_succeed(){
        try ( Neo4jTransaction tx = Neo4jClient.getReadTransaction() ) {
            StatementResult rs = tx.run("CREATE (me:Person { name:$name, born:$born }) RETURN me", parameters( "name", "Benoit", "born", 1983 ));
            Node me = rs.single().get("me").asNode();

            StatementResult rs2 = tx.run("CREATE (movie:Movie { title:$title }) RETURN movie", parameters( "title", "My Favorite film"));
            Node movie = rs2.single().get("movie").asNode();

            tx.run("MATCH (n), (m) WHERE id(n)=$id1 AND id(m)=$id2 CREATE (n)-[:ACTED_IN]->(m) ", parameters( "id1", me.id(), "id2", movie.id()));
            tx.failure();
        }

        StatementResult rs = Neo4jClient.read("MATCH (n:Person { name:$name, born:$born })-[r:ACTED_IN]->(m:Movie { title:$title }) RETURN n,r,m", parameters( "name", "Benoit", "born", 1983, "title", "My Favorite film"));
        Assert.assertEquals(0, rs.list().size());
    }

    @Test
    public void tx_with_run_after_commit_should_failed(){
        try ( Neo4jTransaction tx = Neo4jClient.getReadTransaction() ) {
            StatementResult rs = tx.run("MATCH (actor:Person {name:$name})-[:ACTED_IN]->(movies) RETURN actor,movies", parameters("name", "Tom Hanks"));
            tx.success();
            tx.close();
            tx.run("MATCH (n) RETURN n");
        } catch (RuntimeException e) {
            Assert.assertEquals(RuntimeException.class, e.getClass());
            Assert.assertEquals("Session or transaction is closed", e.getMessage());
        }
    }

}

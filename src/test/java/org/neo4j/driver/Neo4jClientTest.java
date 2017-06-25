package org.neo4j.driver;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.driver.junit.AbstractSingleUnitTest;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;

import java.util.stream.Stream;

import static org.neo4j.driver.v1.Values.parameters;

public class Neo4jClientTest extends AbstractSingleUnitTest {

    @Test
    public void bad_cypher_query_should_result_to_error(){
        try (Stream<Record> rs = Neo4jClient.read("QWERTYUIOP")) {
           // nothing
        }
        catch (Neo4jClientException e) {
            Assert.assertEquals(Neo4jClientException.class, e.getClass());
        }
    }

    @Test
    public void autocommit_read_query_result_should_succeed(){
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN n")) {
            Assert.assertEquals(169, rs.count());
        }
    }

    @Test
    public void autocommit_write_query_should_succeed(){
        try (Stream<Record> rs = Neo4jClient.write("CREATE (me:Person { name:$name, born:$born }) RETURN me", parameters( "name", "Benoit", "born", 1983 ))) {

            Node me = rs.findFirst().get().get("me").asNode();

            Assert.assertTrue(me.hasLabel("Person"));
            Assert.assertEquals("Benoit", me.get("name").asString());
            Assert.assertEquals(1983, me.get("born").asInt());
        }


        // Reset modifications
        Neo4jClient
                .write("MATCH (me:Person { name:$name, born:$born }) DELETE me", parameters( "name", "Benoit", "born", 1983 ))
                .close();
        this.autocommit_read_query_result_should_succeed();
    }

    @Test
    public void tx_read_with_commit_should_succeed(){
        try ( Neo4jTransaction tx = Neo4jClient.getReadTransaction() ) {
            Stream<Record> rs = tx.run("MATCH (actor:Person {name:$name})-[:ACTED_IN]->(movies) RETURN actor,movies", parameters( "name", "Tom Hanks" ));
            tx.success();

            Assert.assertEquals(12, rs.count());
        }
    }

    @Test
    public void tx_write_with_commit_should_succeed(){
        String bookmarkId = null;
        try ( Neo4jTransaction tx = Neo4jClient.getWriteTransaction() ) {
            Stream<Record> rs = tx.run("CREATE (me:Person { name:$name, born:$born }) RETURN me", parameters( "name", "Benoit", "born", 1983 ));
            Node me = rs.findFirst().get().get("me").asNode();

            Stream<Record> rs2 = tx.run("CREATE (movie:Movie { title:$title }) RETURN movie", parameters( "title", "My Favorite film"));
            Node movie = rs2.findFirst().get().get("movie").asNode();

            tx.run("MATCH (n), (m) WHERE id(n)=$id1 AND id(m)=$id2 CREATE (n)-[:ACTED_IN]->(m) ", parameters( "id1", me.id(), "id2", movie.id()));
            tx.success();
            bookmarkId = tx.getBookmarkId();
        }

        Stream<Record> rs = Neo4jClient.read("MATCH (n:Person { name:$name, born:$born })-[r:ACTED_IN]->(m:Movie { title:$title }) RETURN n,r,m", parameters( "name", "Benoit", "born", 1983, "title", "My Favorite film"), bookmarkId);
        Assert.assertEquals(1, rs.count());

        // Reset modifications
        Neo4jClient
                .write("MATCH (n:Person { name:$name, born:$born })-[:ACTED_IN]->(m:Movie { title:$title }) DETACH DELETE n, m", parameters( "name", "Benoit", "born", 1983, "title", "My Favorite film"))
                .close();
        this.autocommit_read_query_result_should_succeed();
    }

    @Test
    public void tx_write_with_rollback_should_succeed(){
        try ( Neo4jTransaction tx = Neo4jClient.getWriteTransaction() ) {
            Stream<Record> rs = tx.run("CREATE (me:Person { name:$name, born:$born }) RETURN me", parameters( "name", "Benoit", "born", 1983 ));
            Node me = rs.findFirst().get().get("me").asNode();

            Stream<Record> rs2 = tx.run("CREATE (movie:Movie { title:$title }) RETURN movie", parameters( "title", "My Favorite film"));
            Node movie = rs2.findFirst().get().get("movie").asNode();

            tx.run("MATCH (n), (m) WHERE id(n)=$id1 AND id(m)=$id2 CREATE (n)-[:ACTED_IN]->(m) ", parameters( "id1", me.id(), "id2", movie.id()));
            tx.failure();
        }

        Stream<Record> rs = Neo4jClient.read("MATCH (n:Person { name:$name, born:$born })-[r:ACTED_IN]->(m:Movie { title:$title }) RETURN n,r,m", parameters( "name", "Benoit", "born", 1983, "title", "My Favorite film"));
        Assert.assertEquals(0, rs.count());
    }

    @Test
    public void tx_with_run_after_commit_should_failed(){
        try ( Neo4jTransaction tx = Neo4jClient.getReadTransaction() ) {
            Stream<Record> rs = tx.run("MATCH (actor:Person {name:$name})-[:ACTED_IN]->(movies) RETURN actor,movies", parameters("name", "Tom Hanks"));
            tx.success();
            tx.close();
            tx.run("MATCH (n) RETURN n");
        } catch (Neo4jClientException e) {
            Assert.assertEquals(Neo4jClientException.class, e.getClass());
            Assert.assertEquals("Session or transaction is closed", e.getMessage());
        }
    }

}

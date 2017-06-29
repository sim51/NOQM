package org.neo4j.driver.projection;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.Neo4jClient;
import org.neo4j.driver.Neo4jClientException;
import org.neo4j.driver.junit.AbstractUnitTest;
import org.neo4j.driver.junit.CustomNeo4jRule;
import org.neo4j.driver.projection.pojo.Movie;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectionTest extends AbstractUnitTest {

    public ProjectionTest() {
        super(new CustomNeo4jRule(CustomNeo4jRule.Mode.SINGLE));
    }

    @Test
    public void test_single_projection_with_not_single_result_should_fail() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN n, 1 AS test")) {
            List<Node> list = rs.map(Projections.create(Node.class)).collect(Collectors.toList());

            Assert.assertEquals(169, list.size());
        } catch (Neo4jClientException e) {
            Assert.assertEquals(Neo4jClientException.class, e.getClass());
            Assert.assertEquals("Record doesn't have a single column -> can't cast it to primitive object type", e.getMessage());
        }

    }

    @Test
    public void test_single_projection_as_string() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n:Movie) RETURN n.title")) {
            List<String> list = rs.map(Projections.create(String.class)).collect(Collectors.toList());

            Assert.assertEquals(38, list.size());
        }
    }

    @Test
    public void test_single_projection_as_node() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN n")) {
            List<Node> list = rs.map(Projections.create(Node.class)).collect(Collectors.toList());

            list.forEach((node) -> {
                Assert.assertNotNull(node);
            });
            Assert.assertEquals(169, list.size());
        }
    }

    @Test
    public void test_single_projection_as_long() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN count(*)")) {
            Long result = rs.map(Projections.create(Long.class)).findFirst().get();
            Assert.assertEquals(Long.valueOf(169), result);
        }
    }

    @Test
    public void test_single_projection_as_integer() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN count(*)")) {
            Integer result = rs.map(Projections.create(Integer.class)).findFirst().get();
            Assert.assertEquals(Integer.valueOf(169), result);
        }
    }

    @Test
    public void test_single_projection_as_double() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN count(*)")) {
            Double result = rs.map(Projections.create(Double.class)).findFirst().get();
            Assert.assertEquals(Double.valueOf(169), result);
        }
    }

    @Test
    public void test_single_projection_as_number() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN count(*)")) {
            Number result = rs.map(Projections.create(Number.class)).findFirst().get();
            Assert.assertEquals(Long.valueOf(169), result);
        }
    }

    @Test
    public void test_single_projection_as_boolean() {
        try (Stream<Record> rs = Neo4jClient.read("RETURN TRUE")) {
            Boolean result = rs.map(Projections.create(Boolean.class)).findFirst().get();
            Assert.assertTrue(result);
        }
    }

    @Test
    public void test_single_projection_as_primmitive_list() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN collect(n) AS nodes")) {
            List<Node> result = rs.map(Projections.create(List.class)).findFirst().get();
            Assert.assertEquals(169, result.size());
            Assert.assertTrue(result.get(0).id() >= 0);
        }
    }

    @Test
    public void test_single_projection_as_pojo() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n:Movie) RETURN n.title AS title, n.tagline AS tagline, n.released AS released")) {
            List<Movie> list = rs.map(Projections.create(Movie.class)).collect(Collectors.toList());

            list.forEach((movie) -> {
                Assert.assertNotNull(movie.title);
                Assert.assertNotNull(movie.tagline);
                Assert.assertNotNull(movie.released);
            });
            Assert.assertEquals(38, list.size());
        }
    }
}

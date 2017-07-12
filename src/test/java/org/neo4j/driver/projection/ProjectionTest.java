package org.neo4j.driver.projection;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.driver.Neo4jClient;
import org.neo4j.driver.exception.Neo4jClientException;
import org.neo4j.driver.junit.AbstractUnitTest;
import org.neo4j.driver.junit.CustomNeo4jRule;
import org.neo4j.driver.projection.pojo.CastingMovie;
import org.neo4j.driver.projection.pojo.Movie;
import org.neo4j.driver.projection.pojo.Person;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.types.Node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectionTest extends AbstractUnitTest {

    public ProjectionTest() {
        super(new CustomNeo4jRule(CustomNeo4jRule.Mode.SINGLE));
    }

    @Test
    public void test_single_projection_with_not_single_result_should_fail() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN n, 1 AS test")) {
            List<Node> list = rs.map(Projections.singleAs(Node.class)).collect(Collectors.toList());

            Assert.assertEquals(169, list.size());
        } catch (Neo4jClientException e) {
            Assert.assertEquals(Neo4jClientException.class, e.getClass());
            Assert.assertEquals("Record doesn't have a single column -> can't cast it to primitive object type", e.getMessage());
        }

    }

    @Test
    public void test_single_projection_as_long() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN count(*)")) {
            Long result = rs.map(Projections.singleAs(Long.class)).findFirst().get();
            Assert.assertEquals(Long.valueOf(169), result);
        }
    }

    @Test
    public void test_single_projection_as_integer() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN count(*)")) {
            Integer result = rs.map(Projections.singleAs(Integer.class)).findFirst().get();
            Assert.assertEquals(Integer.valueOf(169), result);
        }
    }

    @Test
    public void test_single_projection_as_double() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN count(*)")) {
            Double result = rs.map(Projections.singleAs(Double.class)).findFirst().get();
            Assert.assertEquals(Double.valueOf(169), result);
        }
    }

    @Test
    public void test_single_projection_as_number() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN count(*)")) {
            Number result = rs.map(Projections.singleAs(Number.class)).findFirst().get();
            Assert.assertEquals(Long.valueOf(169), result);
        }
    }

    @Test
    public void test_single_projection_as_boolean() {
        try (Stream<Record> rs = Neo4jClient.read("RETURN TRUE")) {
            Boolean result = rs.map(Projections.singleAs(Boolean.class)).findFirst().get();
            Assert.assertTrue(result);
        }
    }

    @Test
    public void test_single_projection_as_string() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n:Movie) RETURN n.title")) {
            List<String> list = rs.map(Projections.singleAs(String.class)).collect(Collectors.toList());

            Assert.assertEquals(38, list.size());
        }
    }

    @Test
    public void test_single_projection_as_node() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN n")) {
            List<Node> list = rs.map(Projections.singleAs(Node.class)).collect(Collectors.toList());

            list.forEach((node) -> {
                Assert.assertNotNull(node);
            });
            Assert.assertEquals(169, list.size());
        }
    }

    @Test
    public void test_single_projection_as_movie() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n:Movie) RETURN n")) {
            List<Movie> list = rs.map(Projections.singleAs(Movie.class)).collect(Collectors.toList());

            list.forEach((movie) -> {
                Assert.assertNotNull(movie);
                Assert.assertNotNull(movie.id);
                Assert.assertEquals(1, movie.labels.size());
                Assert.assertNotNull(movie.title);
            });
            Assert.assertEquals(38, list.size());
        }
    }

    @Test
    public void test_single_projection_as_primitive_list() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN collect(n) AS nodes")) {
            List<Node> result = (List<Node>) rs.map(Projections.singleAsListOf(new ArrayList<Node>() {
            }.getClass())).findFirst().get();
            Assert.assertEquals(169, result.size());
            Assert.assertTrue(result.get(0).id() >= 0);
        }
    }

    @Test
    public void test_single_projection_as_movie_list() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n:Movie) RETURN collect(n) AS movies")) {
            List<Movie> result = (List<Movie>) rs.map(Projections.singleAsListOf(new ArrayList<Movie>(){}.getClass())).findFirst().get();

            Assert.assertEquals(38, result.size());
            result.forEach((movie) -> {
                Assert.assertNotNull(movie.title);
                Assert.assertNotNull(movie.released);
            });
        }
    }

    @Test
    public void test_projection_as_movie() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (n:Movie) RETURN n.title AS title, n.tagline AS tagline, n.released AS released")) {
            List<Movie> list = rs.map(Projections.as(Movie.class)).collect(Collectors.toList());

            Assert.assertEquals(38, list.size());
            list.forEach((movie) -> {
                Assert.assertNotNull(movie.title);
                Assert.assertNotNull(movie.released);
            });
        }
    }

    @Test
    public void test_projection_as_casting_movie() {
        try (Stream<Record> rs = Neo4jClient.read("MATCH (m:Movie)<-[:ACTED_IN]-(p:Person) RETURN m AS movie, collect(p) AS actors")) {
            List<CastingMovie> list = rs.map(Projections.as(CastingMovie.class)).collect(Collectors.toList());

            list.forEach((casting) -> {
                Assert.assertNotNull(casting.movie);
                Assert.assertNotNull(casting.movie.id);
                Assert.assertEquals(1, casting.movie.labels.size());
                Assert.assertNotNull(casting.movie.title);
                Assert.assertNotNull(casting.movie.released);

                Assert.assertNotNull(casting.actors);
                casting.actors.forEach( (actor) -> {
                    Assert.assertNotNull(actor.id);
                    Assert.assertEquals(1, actor.labels.size());
                    Assert.assertNotNull(actor.name);
                });
            });
            Assert.assertEquals(38, list.size());
        }
    }

    @Test
    public void test_projection_with_rule(){
        Map<String, Class> h = new LinkedHashMap<String, Class>() {{
            put("Movie", Movie.class);
            put("Person", Person.class);
        }};

    }
}

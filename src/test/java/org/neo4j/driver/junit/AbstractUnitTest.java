package org.neo4j.driver.junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.neo4j.driver.Neo4jClient;
import org.neo4j.driver.Neo4jClientClusterTest;
import org.neo4j.driver.Neo4jTransaction;
import org.neo4j.driver.v1.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Scanner;

public abstract class AbstractUnitTest {

    @Rule
    public CustomNeo4jRule neo4j;

    public AbstractUnitTest(CustomNeo4jRule rule) {
        this.neo4j = rule;
    }

    @Before
    public void before() throws Exception {
        this.initialize(this.neo4j.getBoltUri());
        this.resetAndLoad();

        // Wait until all nodes as data
        for (String nodeBoltUri : neo4j.getNodesBoltUri()) {
            Boolean stop = Boolean.FALSE;
            while(!stop) {
                if(checkFixtureCount(nodeBoltUri)){
                    stop = Boolean.TRUE;
                }
            }
        }
    }

    @After
    public void after(){
        Neo4jClient.destroy();
    }

    private void initialize(String url) throws Exception {
        //Path path = Files.createTempDirectory("neo");
        File file = new File(AbstractUnitTest.class.getResource("/neo4j-driver-ext.properties").toURI());

        // Update the '-ext' file with the bolt url
        Properties data = new Properties();
        data.setProperty("neo4j.url", url);
        try(FileOutputStream out = new FileOutputStream(file)) {
            data.store(out, null);
        }
    }

    private void resetAndLoad(){
        // load the fixture script
        String query = "";
        try (Scanner s = new Scanner(Neo4jClientClusterTest.class.getResourceAsStream("/cypher/movie.cyp")).useDelimiter("\\n")) {
            while(s.hasNext()) {
                query += s.next() + "\n";
            }
        }
        try(Neo4jTransaction tx = Neo4jClient.getWriteTransaction()) {
            // reset
            tx.run("MATCH (n) DETACH DELETE n");
            // load
            tx.run(query);
            tx.success();
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

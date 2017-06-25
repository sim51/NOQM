package org.neo4j.driver.junit;

import org.junit.AfterClass;
import org.neo4j.driver.Neo4jClient;
import org.neo4j.driver.Neo4jClientClusterTest;
import org.neo4j.driver.Neo4jTransaction;
import org.neo4j.driver.v1.Record;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Stream;

public abstract class AbstractUnitTest {

    public static void initialize(String url) throws Exception {
        //Path path = Files.createTempDirectory("neo");
        File file = new File(AbstractUnitTest.class.getResource("/neo4j-driver-ext.properties").toURI());

        // Update the '-ext' file with the bolt url
        Properties data = new Properties();
        data.setProperty("neo4j.url", url);
        try(FileOutputStream out = new FileOutputStream(file)) {
            data.store(out, null);
        }

        // load the fixture script
        String query = "";
        try (Scanner s = new Scanner(Neo4jClientClusterTest.class.getResourceAsStream("/cypher/movie.cyp")).useDelimiter("\\n")) {
            while(s.hasNext()) {
                query += s.next() + "\n";
            }
        }
        try(Neo4jTransaction tx = Neo4jClient.getWriteTransaction()) {
            tx.run(query);
            tx.success();
        }

        // Wait until a replica as data
        Boolean stop = Boolean.FALSE;
        while(!stop) {
            try(Stream<Record> rs = Neo4jClient.read("MATCH (n) RETURN count(*)")) {
                if(rs.findFirst().get().get(0).asInt() > 0) {
                    stop = Boolean.TRUE;
                }
            }
        }
    }

    @AfterClass
    public static void after() {
        Neo4jClient.destroy();
    }

}

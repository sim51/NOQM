package org.neo4j.driver.junit;

import org.junit.*;
import org.junit.rules.TestRule;
import org.neo4j.driver.Neo4jClient;
import org.neo4j.driver.Neo4jClientClusterTest;
import org.neo4j.driver.Neo4jTransaction;
import org.neo4j.driver.v1.util.cc.ClusterRule;
import org.neo4j.harness.junit.Neo4jRule;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Scanner;

public abstract class AbstractSingleUnitTest extends AbstractUnitTest {

    @ClassRule
    public static Neo4jRule neo4j = new Neo4jRule();

    @BeforeClass
    public static void before() throws Exception {
        initialize(neo4j.boltURI().toString());
    }

}

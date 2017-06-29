package org.neo4j.driver.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.neo4j.driver.v1.util.cc.ClusterMember;
import org.neo4j.driver.v1.util.cc.ClusterRule;
import org.neo4j.harness.junit.Neo4jRule;

import java.util.ArrayList;
import java.util.List;

public class CustomNeo4jRule implements TestRule {

    public enum Mode {SINGLE, CLUSTER}

    ;

    public TestRule neo4jRule;
    public Mode     mode;

    public CustomNeo4jRule(Mode mode) {
        this.mode = mode;
        if (this.mode == Mode.SINGLE) {
            neo4jRule = new Neo4jRule();
        } else {
            neo4jRule = new ClusterRule();
        }
    }

    @Override public Statement apply(Statement statement, Description description) {
        return this.neo4jRule.apply(statement, description);
    }

    public String getBoltUri() {
        String uri = null;
        if (this.mode == Mode.SINGLE) {
            Neo4jRule rule = (Neo4jRule) this.neo4jRule;
            uri = rule.boltURI().toString();
        } else {
            ClusterRule rule = (ClusterRule) this.neo4jRule;
            uri = rule.getCluster().leader().getRoutingUri().toString();
        }
        return uri;
    }

    public List<String> getNodesBoltUri() {
        List<String> uris = new ArrayList<>();

        if (this.mode == Mode.SINGLE) {
            Neo4jRule rule = (Neo4jRule) this.neo4jRule;
            uris.add(rule.boltURI().toString());
        } else {
            ClusterRule rule = (ClusterRule) this.neo4jRule;
            for (ClusterMember member : rule.getCluster().members()) {
                uris.add(member.getBoltUri().toString());
            }
        }

        return uris;
    }

}
package org.neo4j.driver;

import org.neo4j.driver.v1.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Configuration {

    /**
     * Map with all the key/value from properties files.
     */
    private Map<String, Object> config = new HashMap<>();

    /**
     * Construction that parse properties files.
     */
    public Configuration() {
        // Loading neo4j-driver.properties
        Properties prop = new Properties();

        try(InputStream stream = this.getClass().getClassLoader().getResourceAsStream("neo4j-driver.properties")) {
            prop.load(stream);
            for (final String name: prop.stringPropertyNames()) {
                this.config.put(name, prop.getProperty(name));
            }
        } catch (IOException e) {
            throw new RuntimeException("File neo4j-driver.properties not found !!!");
        }

        // Loading neo4j-driver-ext.properties to override default config
        Properties propExt = new Properties();

        try (InputStream streamExt = this.getClass().getClassLoader().getResourceAsStream("neo4j-driver-ext.properties")){
            propExt.load(streamExt);
            for (final String name: propExt.stringPropertyNames()) {
                this.config.put(name, propExt.getProperty(name));
            }
        } catch (Exception e) {
            // silent exception, we don't care
        }
    }

    /**
     * Convert this object as a Neo4j driver configuration object.
     */
    public Config toDriverConfig() {
        Config.ConfigBuilder cb = Config.build();

        if( this.config.containsKey("neo4j.maxIdleConnectionPoolSize") ) {
            cb.withMaxIdleSessions(Integer.valueOf((String) this.config.get("neo4j.maxIdleConnectionPoolSize")));
        }
        if( this.config.containsKey("neo4j.idleTimeBeforeConnectionTest") ) {
            cb.withConnectionLivenessCheckTimeout(Long.valueOf((String) this.config.get("neo4j.idleTimeBeforeConnectionTest")), TimeUnit.SECONDS);
        }
        if( this.config.containsKey("neo4j.logLeakedSessions") ) {
            if(Boolean.valueOf((String) this.config.get("neo4j.logLeakedSessions"))) {
                cb.withLeakedSessionsLogging();
            }
        }
        if( this.config.containsKey("neo4j.encrypted") ) {
            if(Boolean.valueOf((String) this.config.get("neo4j.encrypted"))) {
                cb.withEncryption();
            }
            else {
                cb.withoutEncryption();
            }
        }
        if( this.config.containsKey("neo4j.connectionTimeoutMillis") ) {
            cb.withConnectionTimeout(Long.valueOf((String) this.config.get("neo4j.connectionTimeoutMillis")), TimeUnit.MILLISECONDS);
        }
        if( this.config.containsKey("neo4j.retrySettings") ) {
            cb.withMaxTransactionRetryTime(Long.valueOf((String) this.config.get("neo4j.retrySettings")), TimeUnit.SECONDS);
        }
        if( this.config.containsKey("neo4j.trustStrategy") ) {
            switch (this.getStringOrDefault("neo4j.trustStrategy", "").toUpperCase()) {
                case "ALL" :
                    cb.withTrustStrategy(Config.TrustStrategy.trustAllCertificates());
                    break;
                case "SYSTEM" :
                    cb.withTrustStrategy(Config.TrustStrategy.trustSystemCertificates());
                    break;
                default:
                    File cert = new File(this.getStringOrDefault("neo4j.trustStrategy", ""));
                    if (cert.exists()) {
                        cb.withTrustStrategy(Config.TrustStrategy.trustCustomCertificateSignedBy(cert));
                    }
                    else{
                        throw new RuntimeException("Certificate not found");
                    }

            }



        }

        return cb.toConfig();
    }

    /**
     * Return a config property from the map as a String.
     */
    public String getStringOrDefault(String key, String value) {
        return (String) this.config.getOrDefault(key, value);
    }
}

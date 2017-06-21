package org.neo4j.driver;

import javax.management.IntrospectionException;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Scanner;

public class TestHelper {

    public static void initDb(String connectionUrl) throws Exception {
        //Path path = Files.createTempDirectory("neo");
        File file = new File(TestHelper.class.getResource("/neo4j-driver-ext.properties").toURI());

        // Update the '-ext' file with the bolt url
        Properties data = new Properties();
        data.setProperty("neo4j.url", connectionUrl);
        try(FileOutputStream out = new FileOutputStream(file)) {
            data.store(out, null);
        }
        System.out.println("Writing ext file with url " + connectionUrl);

        //addURLToSystemClassLoader(path.toUri().toURL());

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

    }

    public static void addURLToSystemClassLoader(URL url) throws IntrospectionException {
        URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> classLoaderClass = URLClassLoader.class;

        try {
            Method method = classLoaderClass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(systemClassLoader, new Object[]{url});
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IntrospectionException("Error when adding url to system ClassLoader ");
        }
    }

}

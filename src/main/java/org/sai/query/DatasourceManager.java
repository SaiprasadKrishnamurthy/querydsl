package org.sai.query;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by saipkri on 09/08/17.
 */
public class DatasourceManager {
    private static EntityManagerFactory emf;
    public static EntityManager em;
    private static final String dbName = "qdsl";
    public static Morphia morphia = new Morphia();
    public static Datastore datastore;

    static {
        emf = Persistence.createEntityManagerFactory("org.sai.querydsl.intro");
        em = emf.createEntityManager();
        morphia.mapPackage("org.sai.entity");
        try {
            MongoClient client = MongodForTestsFactory
                    .with(Version.Main.PRODUCTION).newMongo();
            datastore = morphia.createDatastore(client, dbName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hide me
    private DatasourceManager() {
    }
}

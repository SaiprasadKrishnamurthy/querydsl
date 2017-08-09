package org.sai.qf;

import com.mongodb.MongoClient;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.mongodb.morphia.MorphiaQuery;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.net.UnknownHostException;

/**
 * Created by saipkri on 09/08/17.
 */
public class QueryFactoryProvider {
    private static EntityManagerFactory emf;
    public static EntityManager em;
    private static JPAQueryFactory jpaQueryFactory;
    private static final String dbName = "qdsl";
    private static Morphia morphia = new Morphia();
    private static Datastore datastore;

    static {
        emf = Persistence.createEntityManagerFactory("org.sai.querydsl.intro");
        em = emf.createEntityManager();
        jpaQueryFactory = new JPAQueryFactory(em);
        morphia.mapPackage("org.baeldung.entity");
        try {
            MongoClient mongoClient = new MongoClient("localhost");
            datastore = morphia.createDatastore(mongoClient, dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    // Hide me
    private QueryFactoryProvider() {
    }

    public static JPAQuery<?> jpaQuery() {
        return jpaQueryFactory.query();
    }

    @SuppressWarnings("unchecked")
    public static MorphiaQuery<?> mongoQuery(final EntityPath<?> document) {
        return new MorphiaQuery(morphia, datastore, document);
    }
}

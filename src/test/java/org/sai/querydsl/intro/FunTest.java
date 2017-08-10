/*
 * (c) Центр ИТ, 2016. Все права защищены.
 */
package org.sai.querydsl.intro;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sai.entity.Customer;
import org.sai.entity.Order;
import org.sai.entity.QCustomer;
import org.sai.entity.QOrder;
import org.sai.query.DatasourceManager;
import org.sai.query.JPAQueryTemplate;
import org.sai.query.MongoQueryTemplate;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * A Simple way to test our scenarios.
 * @author sai kris
 */
public class FunTest {

    private static final int TOTAL_ROWS = 100;
    private JPAQueryTemplate jpaQueryTemplate;

    private static void createDataJPA(Consumer<EntityManager> entityManagerConsumer) {
        EntityManager em = DatasourceManager.em;
        em.getTransaction().begin();
        entityManagerConsumer.accept(em);
        em.getTransaction().commit();
    }

    private static void createDataMongo(final Object document) {
        DatasourceManager.datastore.save(document);
    }

    @BeforeClass
    public static void populateDatabase() {

        String[] names = "Sai,Kris,Jackie,Jane".split(",");
        String[] cities = "Bangalore,London,Madras,Dublin,Delhi".split(",");

        createDataJPA((em) -> {
            Random rd = new Random();
            for (long i = 0; i < TOTAL_ROWS; i++) {
                Customer customer = new Customer(i, names[rd.nextInt(names.length)], cities[rd.nextInt(cities.length)]);
                em.persist(customer);
                createDataMongo(customer);
                Order order = new Order(i, i, Double.parseDouble(rd.nextInt(100) + ""), "ONLINE");
                createDataMongo(order);
                em.persist(order);
            }
        });
    }

    @Before
    public void setup() {
        jpaQueryTemplate = new JPAQueryTemplate(DatasourceManager.em);
    }

    @Test
    public void selectAllCustomers() {
        QCustomer customerModel = QCustomer.customer;
        List<Customer> customers = jpaQueryTemplate.query()
                .select(customerModel)
                .from(customerModel)
                .fetch();
        assertNotNull(customers);
        assertEquals(TOTAL_ROWS, customers.size());
    }

    @Test
    public void selectAllCustomersWithCityFilter() {
        QCustomer customerModel = QCustomer.customer;
        List<Customer> customers = jpaQueryTemplate.query()
                .select(customerModel)
                .from(customerModel)
                .where(customerModel.city.eq("Bangalore"))
                .fetch();
        assertNotNull(customers);
        assertTrue(customers.size() > 0);
    }

    @Test
    public void selectAllCustomersWithJoinedWithOrders() {
        QCustomer customerModel = QCustomer.customer;
        QOrder orderModel = QOrder.order;

        List<Tuple> result = jpaQueryTemplate.query()
                .from(customerModel)
                .select(customerModel.name, customerModel.city, orderModel.channel, orderModel.amount)
                .innerJoin(orderModel)
                .on(customerModel.id.eq(orderModel.id))
                .where(customerModel.city.eq("Bangalore"))
                .fetch();

        assertNotNull(result);
        assertTrue(result.size() > 0);
        result.forEach(tuple -> {
            assertEquals(4, tuple.size());
            assertEquals("Bangalore", tuple.get(1, String.class));
        });
    }

    @Test
    public void selectAllCustomersAndTheirTotalOrderAmount() {
        QCustomer customerModel = QCustomer.customer;
        QOrder orderModel = QOrder.order;

        NumberPath<Double> total = Expressions.numberPath(Double.class, "total");

        List<Tuple> result = jpaQueryTemplate.query()
                .from(customerModel)
                .select(customerModel.name, orderModel.amount.sum().as(total))
                .innerJoin(orderModel)
                .on(customerModel.id.eq(orderModel.id))
                .groupBy(customerModel.name)
                .having(orderModel.amount.sum().gt(1300))
                .orderBy(total.desc())
                .fetch();

        System.out.println(result);
    }

    @Test
    public void selectCustomerNameStartsWithMongo() {
        QCustomer customerModel = QCustomer.customer;
        MongoQueryTemplate<Customer, QCustomer> mongoQueryTemplate = new MongoQueryTemplate<>(DatasourceManager.morphia, DatasourceManager.datastore);
        List<Customer> customers = mongoQueryTemplate.query(customerModel)
                .where(customerModel.city.startsWith("D"))
                .fetch();
        assertTrue(customers.size() > 0);
    }

    @Test
    public void mongoJoin() {
        QCustomer customerModel = QCustomer.customer;
        QOrder orderModel = QOrder.order;
        MongoQueryTemplate<Customer, QCustomer> mongoQueryTemplate = new MongoQueryTemplate<>(DatasourceManager.morphia, DatasourceManager.datastore);
        List<Customer> customers = mongoQueryTemplate.query(customerModel)
                .join(customerModel.id, orderModel.customerId)
                .on(customerModel.city.isNotNull())
                .fetch();
        System.out.println(customers);
    }


    @AfterClass
    public static void teardown() {
        DatasourceManager.em.close();
    }
}

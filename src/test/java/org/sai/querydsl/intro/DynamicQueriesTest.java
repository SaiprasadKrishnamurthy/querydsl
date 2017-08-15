/*
 * (c) Центр ИТ, 2016. Все права защищены.
 */
package org.sai.querydsl.intro;

import com.querydsl.core.Tuple;
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

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.sai.querydsl.intro.ExpressionUtil.*;

/**
 * A Simple way to test our scenarios.
 *
 * @author sai kris
 */
public class DynamicQueriesTest {

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
                .where(applyFilter(field(customerModel, "city"), new Operation<>("in", Arrays.asList("Bangalore", "London"))))
                .fetch();
        System.out.println(" --------- ");
        System.out.println(customers);
        assertNotNull(customers);
        assertTrue(customers.size() > 0);
    }

    @Test
    public void selectAllCustomersWithJoinedWithOrders() {
        QCustomer customerModel = QCustomer.customer;
        QOrder orderModel = QOrder.order;

        List<Tuple> result = jpaQueryTemplate.query()
                .from(customerModel)
                .select(field(customerModel, "name"),
                        field(customerModel, "city"),
                        field(orderModel, "amount"),
                        field(orderModel, "channel"))
                .innerJoin(orderModel)
                .on(innerJoinOn(field(customerModel, "id"), field(orderModel, "customerId")))
                .where(applyFilter(field(customerModel, "city"), new Operation<>("=", Arrays.asList("Bangalore"))))
                .fetch();

        assertNotNull(result);
        assertTrue(result.size() > 0);
        result.forEach(tuple -> {
            assertEquals(4, tuple.size());
            assertEquals("Bangalore", tuple.get(1, String.class));
        });
    }

    @AfterClass
    public static void teardown() {
        DatasourceManager.em.close();
    }
}

/*
 * (c) Центр ИТ, 2016. Все права защищены.
 */
package org.sai.querydsl.intro;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sai.entity.Customer;
import org.sai.entity.Order;
import org.sai.entity.QCustomer;
import org.sai.entity.QOrder;
import org.sai.qf.QueryFactoryProvider;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class FunTest {

    private static final int TOTAL_ROWS = 100;

    private static void createData(Consumer<EntityManager> entityManagerConsumer) {
        EntityManager em = QueryFactoryProvider.em;
        em.getTransaction().begin();
        entityManagerConsumer.accept(em);
        em.getTransaction().commit();
    }

    @BeforeClass
    public static void populateDatabase() {

        String[] names = "Sai,Kris,Jackie,Jane".split(",");
        String[] cities = "Bangalore,London,Madras,Dublin".split(",");

        createData((em) -> {
            Random rd = new Random();
            for (long i = 0; i < TOTAL_ROWS; i++) {
                Customer customer = new Customer(i, names[rd.nextInt(names.length)], cities[rd.nextInt(cities.length)]);
                em.persist(customer);
                Order order = new Order(i, i, Double.parseDouble(rd.nextInt(100) + ""), "ONLINE");
                em.persist(order);
            }
        });
    }

    @Test
    public void selectAllCustomers() {

        QCustomer customerModel = QCustomer.customer;
        List<Customer> customers = QueryFactoryProvider.jpaQuery().select(customerModel)
                .from(customerModel)
                .fetch();
        assertNotNull(customers);
        assertEquals(TOTAL_ROWS, customers.size());
    }

    @Test
    public void selectAllCustomersWithCityFilter() {
        QCustomer customerModel = QCustomer.customer;
        List<Customer> customers = QueryFactoryProvider.jpaQuery()
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

        List<Tuple> result = QueryFactoryProvider.jpaQuery().from(customerModel)
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

        List<Tuple> result = QueryFactoryProvider.jpaQuery().from(customerModel)
                .select(customerModel.name, orderModel.amount.sum().as(total))
                .innerJoin(orderModel)
                .on(customerModel.id.eq(orderModel.id))
                .groupBy(customerModel.name)
                .having(orderModel.amount.sum().gt(1300))
                .orderBy(total.desc())
                .fetch();

        System.out.println(result);
    }


    @AfterClass
    public static void teardown() {
        QueryFactoryProvider.em.close();
    }
}

package com.proteuez.hibernate.domain;

import org.hamcrest.CoreMatchers;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HibernateSandboxTestConfig.class)
@Transactional(propagation = Propagation.REQUIRED)
@TransactionConfiguration(defaultRollback = true)
public class MainOrderTest {
    @Resource
    private SessionFactory sessionFactory;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Test
    public void sjekkLoadOneToMany() {
        final MainOrder object = new MainOrder();
        object.add(new UnstructuredOrderLine("4 pilz og en pizza"));
        object.add(new UnstructuredOrderLine("Ny telefon"));
        sessionFactory.getCurrentSession().save(object);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        //Test-switch ordinal to ensure correct persistent sorting
        update(0, "telefon");
        update(1, "pilz");

        @SuppressWarnings(value = "unchecked")
        List<MainOrder> orders = sessionFactory.getCurrentSession().createCriteria(MainOrder.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        assertThat(orders, notNullValue());
        assertThat(orders.size(), is(1));
        MainOrder order = orders.get(0);
        assertThat(order.getOrderLines(), notNullValue());
        assertThat(order.getOrderLines().size(), is(2));
        assertThat(project(order.getOrderLines(), String.class, on(UnstructuredOrderLine.class).getOrderDescription()), is(asList("Ny telefon", "4 pilz og en pizza")));

    }

    @Test
    public void checkStaticTypeFactoryRegistration() {
        MainOrder order = new MainOrder();
        final LocalDate storedDate = new LocalDate(2001, 1, 1);
        order.setDatePlaced(storedDate);

        DateTime storedTime = new DateTime(new Date());
        order.setTimePlaced(storedTime);
        sessionFactory.getCurrentSession().save(order);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        order = (MainOrder) sessionFactory.getCurrentSession().get(MainOrder.class, order.getId());

        assertThat(order.getDatePlaced(), is(storedDate));
        assertThat(order.getTimePlaced(), is(storedTime));
    }

    private void update(final int ordinal, final String pattern) {
        assertThat(jdbcTemplate.update("update order_line set ordinal = ? where order_description like ?", ordinal, "%" + pattern + "%"), CoreMatchers.is(1));
    }


}

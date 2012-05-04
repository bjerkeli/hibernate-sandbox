package com.proteuez.hibernate.domain;

import ch.lambdaj.group.StringGroupCondition;
import com.google.common.collect.Lists;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = HibernateSandboxTestConfig.class)
@Transactional(propagation = Propagation.REQUIRED)
@TransactionConfiguration(defaultRollback = true)
public class JdbcTemplatePerformanceTest {
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private SessionFactory sessionFactory;

    private List<Long> orderLines = new ArrayList<Long>();

    //@Before
    public void before() {
        StopWatch watch = new StopWatch();
        watch.start();
        MainOrder order = new MainOrder();
        sessionFactory.getCurrentSession().save(order);
        final int numOrderLines = 10000;
        for (int i = 1; i < numOrderLines; i++) {
            final OrderLine line = new UnstructuredOrderLine("line");
            order.add(line);
            if (i % 1000 == 0)
                System.out.printf("Saved %d", i);

            orderLines.add(line.getId());
            sessionFactory.getCurrentSession().save(line);
        }
        watch.stop();
        System.out.printf("Created %d orderlines in %s", numOrderLines, watch);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
    }


    @Test
    public void batchSaveAll() {
        batchSaveTest(0);
    }

    @Test
    public void batchSave1000() {
        batchSaveTest(1000);
    }

    @Test
    public void batchSave10000() {
        batchSaveTest(10000);
    }

    private void batchSaveTest(final Integer chunkSize) {
        StopWatch watch = new StopWatch();
        watch.start();
        saveWithChunksOf(chunkSize);
        watch.stop();
        System.out.printf("Updated %d orderlines with chunk size %d in %s", orderLines.size(), chunkSize, watch);
    }

    private void saveWithChunksOf(Integer chunkSize) {
        List<List<Long>> targetLines = Lists.partition(orderLines, chunkSize > 0 ? chunkSize : orderLines.size());
        for (final List<Long> targetLine : targetLines) {
            jdbcTemplate.batchUpdate("update order_line set order_description = ? where id = ?", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, String.valueOf(i));
                    ps.setLong(2, targetLine.get(i));
                }

                @Override
                public int getBatchSize() {
                    return targetLine.size();
                }
            });

        }

    }


    @Test
    public void sjekkList() {

        List<String> full = asList("1", "2", "3");
        for (List<String> strings : Lists.partition(full, 2)) {
            System.out.println(strings + ", Size :" + strings.size());
        }


    }


}

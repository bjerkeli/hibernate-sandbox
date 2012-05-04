package com.proteuez.hibernate.domain;

import oracle.jdbc.pool.OracleDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.jadira.usertype.dateandtime.joda.PersistentDateTime;
import org.jadira.usertype.dateandtime.joda.PersistentLocalDate;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 */
@ComponentScan(basePackages = {
        "com.proteuez.hibernate"
})

@Configuration
public class HibernateSandboxTestConfig {

    boolean useOracle = true;

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        return useOracle ? createOracleDataSource() : createInMemDataSource();
    }

    private DataSource createInMemDataSource() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new SingleConnectionDataSource("jdbc:hsqldb:mem:test", "sa", "", true);
    }

    public DataSource createOracleDataSource() {
        OracleDataSource dataSource = null;
        try {
            dataSource = new OracleDataSource();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        dataSource.setURL(System.getProperty("datasource.url"));
        dataSource.setUser(System.getProperty("datasource.password"));
        dataSource.setPassword(System.getProperty("datasource.username"));
        return dataSource;
    }

    @Bean
    public NamingStrategy namingStrategy() {
        return new ImprovedNamingStrategy();
    }

    @Bean
    public SessionFactory sessionFactory() {
        String[] packages =
                {
                        "com.proteuez.hibernate"
                };

        final LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource());
        builder.setNamingStrategy(namingStrategy());
        builder.scanPackages(packages);
        builder.registerTypeOverride(new PersistentLocalDate(), new String[]{"localDate", LocalDate.class.getName()});
        builder.registerTypeOverride(new PersistentDateTime(), new String[]{"dateTime", DateTime.class.getName()});

        return builder.addProperties(createHibernateProperties()).buildSessionFactory();
    }


    @Bean
    public HibernateTransactionManager transactionManager() throws Exception {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory());
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }


    private Properties createHibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("hibernate.dialect", useOracle ? "org.hibernate.dialect.Oracle10gDialect" : "org.hibernate.dialect.HSQLDialect");
        properties.put("hibernate.show_sql", "false");
        properties.put("hibernate.jdbc.batch_size", "1");
        properties.put("hibernate.id.new_generator_mappings", "true");
        return properties;
    }

}
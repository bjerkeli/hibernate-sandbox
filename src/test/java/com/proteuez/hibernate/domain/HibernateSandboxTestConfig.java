package com.proteuez.hibernate.domain;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.cfg.NamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 *
 */
@ComponentScan(basePackages = {
        "com.proteuez.hibernate"
})

@Configuration
public class HibernateSandboxTestConfig {

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new SingleConnectionDataSource("jdbc:hsqldb:mem:test", "sa", "", true);
    }

    @Bean
    public NamingStrategy namingStrategy() {
        return new ImprovedNamingStrategy();
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        String[] packages =
                {
                        "com.proteuez.hibernate"
                };

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setNamingStrategy(namingStrategy());
        sessionFactory.setPackagesToScan(packages);
        sessionFactory.setHibernateProperties(createHibernateProperties());

        return sessionFactory;
    }


    @Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }


    private Properties createHibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.jdbc.batch_size", "1");
        properties.put("hibernate.id.new_generator_mappings", "true");
        return properties;
    }

}
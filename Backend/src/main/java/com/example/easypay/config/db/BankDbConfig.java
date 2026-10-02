package com.example.easypay.config.db;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.easypay.bank.repository",
        entityManagerFactoryRef = "bankEntityManagerFactory",
        transactionManagerRef = "bankTransactionManager"
)
public class BankDbConfig {

	@Bean(name = "bankDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.bank")
	com.zaxxer.hikari.HikariDataSource bankDataSource() {
	    return new com.zaxxer.hikari.HikariDataSource();
	}

    @Bean(name = "bankEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean bankEntityManagerFactory(
            @Qualifier("bankDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean emf =
                new LocalContainerEntityManagerFactoryBean();

        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.example.easypay.bank.entity");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        emf.setJpaPropertyMap(props);
        return emf;
    }

    @Bean(name = "bankTransactionManager")
    PlatformTransactionManager bankTransactionManager(
            @Qualifier("bankEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}

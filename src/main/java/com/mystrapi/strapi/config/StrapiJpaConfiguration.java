package com.mystrapi.strapi.config;

import com.mystrapi.strapi.persistance.entity.strapi.User;
import com.mystrapi.strapi.persistance.repository.strapi.UserRepository;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.SharedCacheMode;
import org.hibernate.cfg.AvailableSettings;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 支持多数据源
 *
 * @author tangqiang
 */
@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(entityManagerFactoryRef = "strapiEntityManagerFactory", transactionManagerRef = "strapiTransactionManager",
        basePackageClasses = UserRepository.class,
        bootstrapMode = BootstrapMode.LAZY)
@EnableTransactionManagement
public class StrapiJpaConfiguration {

    @Bean(name = "strapiDataSourceProperties")
    @ConfigurationProperties("strapi.datasource")
    public DataSourceProperties strapiDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "strapiDataSource")
    @ConfigurationProperties("strapi.datasource.configuration")
    public HikariDataSource hikariDataSource(@Qualifier("strapiDataSourceProperties") @NotNull DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "strapiJpaProperties")
    @ConfigurationProperties("strapi.jpa")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Primary
    @Bean(name = "strapiHibernateProperties")
    @ConfigurationProperties("strapi.jpa.hibernate")
    public HibernateProperties hibernateProperties() {
        return new HibernateProperties();
    }

    @Bean(name = "strapiJpaVendorAdapter")
    @Primary
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean(name = "strapiEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(@Qualifier("strapiDataSource") DataSource dataSource,
                                                                                         @Qualifier("strapiJpaProperties") @NotNull JpaProperties jpaProperties,
                                                                                         @Qualifier("strapiHibernateProperties") @NotNull HibernateProperties hibernateProperties,
                                                                                         @Qualifier("strapiJpaVendorAdapter") JpaVendorAdapter jpaVendorAdapter) {
        jpaProperties.getProperties().put(AvailableSettings.JAKARTA_SHARED_CACHE_MODE, SharedCacheMode.ENABLE_SELECTIVE.name());
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(jpaVendorAdapter,
                // 融合jpa配置和hibernate配置
                hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings()),
                null);
        return builder.dataSource(dataSource).packages(User.class).persistenceUnit("strapiDs").build();
    }

    @Bean(name = "strapiTransactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("strapiEntityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        JpaTransactionManager tx = new JpaTransactionManager();
        tx.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());
        return tx;
    }

}

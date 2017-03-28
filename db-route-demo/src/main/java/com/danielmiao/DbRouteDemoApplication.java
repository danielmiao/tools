package com.danielmiao;

import com.danielmiao.repository.domain.MemberBaseEntity;
import com.danielmiao.route.DataBaseRoute;
import com.danielmiao.route.DataBaseRouteRegister;
import com.danielmiao.route.SplitInterceptor;
import com.danielmiao.service.MemberBaseService;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@EnableJpaRepositories
@EnableAspectJAutoProxy
@SpringBootApplication
public class DbRouteDemoApplication {

    static {
        DataBaseRouteRegister.registClass(MemberBaseEntity.class, "id");
    }

    @Bean
    public DataBaseRoute dataBaseRoute() {
        DataSource source1 = new DataSource();
        source1.setDriverClassName("com.mysql.jdbc.Driver");
        source1.setUrl("jdbc:mysql://127.0.0.1:3306/member_1");
        source1.setUsername("member");
        source1.setPassword("123456");

        DataSource source2 = new DataSource();
        source2.setDriverClassName("com.mysql.jdbc.Driver");
        source2.setUrl("jdbc:mysql://127.0.0.1:3306/member_2");
        source2.setUsername("member");
        source2.setPassword("123456");

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(0, source1);
        dataSourceMap.put(1, source2);

        DataBaseRoute dataBaseRoute = new DataBaseRoute();
        dataBaseRoute.setTargetDataSources(dataSourceMap);
        dataBaseRoute.setDefaultTargetDataSource(source1);

        return dataBaseRoute;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(DataBaseRoute dataBaseRoute) {

        Properties properties = new Properties();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
        properties.put("hibernate.show.sql", "true");

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataBaseRoute);
        factory.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        factory.setJpaProperties(properties);
        factory.setPersistenceUnitName("member");
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        return localContainerEntityManagerFactoryBean.getObject();
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setEntityManagerFactory(entityManagerFactory);
        return manager;
    }

    @Bean
    public SplitInterceptor splitInterceptor(){
        return new SplitInterceptor(1);
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DbRouteDemoApplication.class, args);
        MemberBaseService repository = context.getBean(MemberBaseService.class);
        MemberBaseEntity member = new MemberBaseEntity();
        member.setId(1);
        member.setNick("one");
        repository.save(member);

        member = new MemberBaseEntity();
        member.setId(2);
        member.setNick("two");
        repository.save(member);
    }
}

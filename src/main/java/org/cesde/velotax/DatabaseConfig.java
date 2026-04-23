package org.cesde.velotax;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "app.database", havingValue = "mysql", matchIfMissing = false)
    public DataSource mysqlDataSource(
            @Value("${spring.datasource.mysql.url}") String url,
            @Value("${spring.datasource.mysql.username}") String username,
            @Value("${spring.datasource.mysql.password}") String password,
            @Value("${spring.datasource.mysql.driver-class-name}") String driver) {
        return DataSourceBuilder.create()
                .driverClassName(driver)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.database", havingValue = "sqlserver")
    public DataSource sqlserverDataSource(
            @Value("${spring.datasource.sqlserver.url}") String url,
            @Value("${spring.datasource.sqlserver.username}") String username,
            @Value("${spring.datasource.sqlserver.password}") String password,
            @Value("${spring.datasource.sqlserver.driver-class-name}") String driver) {
        return DataSourceBuilder.create()
                .driverClassName(driver)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
}

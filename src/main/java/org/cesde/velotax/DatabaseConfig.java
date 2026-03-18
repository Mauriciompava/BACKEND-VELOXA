package org.cesde.velotax;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @ConditionalOnProperty(name = "app.database", havingValue = "sqlserver", matchIfMissing = true)
    public DataSource dataSource(
            @Value("${spring.datasource.sqlserver.url}") String url,
            @Value("${spring.datasource.sqlserver.username}") String username,
            @Value("${spring.datasource.sqlserver.password}") String password,
            @Value("${spring.datasource.sqlserver.driver-class-name}") String driver) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driver);
        return dataSource;
    }

    @Bean
    @ConditionalOnProperty(name = "app.database", havingValue = "mysql")
    public DataSource mysqlDataSource(
            @Value("${spring.datasource.mysql.url}") String url,
            @Value("${spring.datasource.mysql.username}") String username,
            @Value("${spring.datasource.mysql.password}") String password,
            @Value("${spring.datasource.mysql.driver-class-name}") String driver) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driver);
        return dataSource;
    }
}
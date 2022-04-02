package com.anthonyguidotti.forum;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@TestConfiguration
public class DatabaseConfig {
    private final String datasourceUrl;

    public DatabaseConfig(
            @Value("${SPRING_DATASOURCE_URL}") String datasourceUrl
    ) {
        this.datasourceUrl = datasourceUrl;
    }

    @Bean
    NamedParameterJdbcTemplate jdbcTemplate() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(datasourceUrl);

        return new NamedParameterJdbcTemplate(dataSource);
    }

}

package net.shyshkin.study.webflux.userservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSetupService implements CommandLineRunner {

    @Value("classpath:h2/init.sql")
    private Resource initSql;

    private final R2dbcEntityTemplate entityTemplate;

    @Override
    public void run(String... args) throws Exception {

        String query = StreamUtils.copyToString(initSql.getInputStream(), StandardCharsets.UTF_8);
        log.debug("{}", query);
        entityTemplate
                .getDatabaseClient()
                .sql(query)
                .then()
                .subscribe();
    }
}

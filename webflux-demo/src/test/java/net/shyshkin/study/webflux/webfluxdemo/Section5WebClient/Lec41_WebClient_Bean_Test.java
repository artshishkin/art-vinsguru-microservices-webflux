package net.shyshkin.study.webflux.webfluxdemo.Section5WebClient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.BaseTest;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Lec41_WebClient_Bean_Test extends BaseTest {

    @Autowired
    WebClient webClient;

    @Test
    void findSquare_blocking() {
        //given
        int input = 16;

        //when
        Response response = webClient
                .get()
                .uri("/reactive-math/square/{input}", input)
                .retrieve()
                .bodyToMono(Response.class)
                .block();

        //then
        log.debug("Received: {}", response);
    }

    @Test
    void findSquare_blocking_toEntity() {
        //given
        int input = 16;

        //when
        ResponseEntity<Response> responseEntity = webClient
                .get()
                .uri("/reactive-math/square/{input}", input)
                .retrieve()
                .toEntity(Response.class)
                .block();

        //then
        log.debug("[{}] {}", responseEntity.getStatusCode(), responseEntity.getBody());
    }

    @Test
    void findSquare_mono_toEntity() throws InterruptedException {

        //given
        int input = 16;
        CountDownLatch latch = new CountDownLatch(1);

        //when
        Mono<ResponseEntity<Response>> responseEntityMono = webClient
                .get()
                .uri("/reactive-math/square/{input}", input)
                .retrieve()
                .toEntity(Response.class);

        //then
        responseEntityMono
                .subscribe(
                        responseEntity -> log.debug("[{}] {}", responseEntity.getStatusCode(), responseEntity.getBody()),
                        ex -> log.error("{}:{}", ex.getClass().getName(), ex.getMessage()),
                        () -> {
                            latch.countDown();
                            log.debug("Completed");
                        });

        latch.await(1, TimeUnit.SECONDS);
    }
}

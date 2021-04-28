package net.shyshkin.study.webflux.webfluxdemo.Section5WebClient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.BaseTest;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class Lec49_HandlingBadRequest_Test extends BaseTest {

    @Autowired
    WebClient webClient;

    @Test
    void badRequestTest_body() {
        //given
        int input = 4;

        //when
        Mono<Response> responseMono = webClient
                .get()
                .uri("/reactive-math/square/{input}/throw", input)
                .retrieve()
                .bodyToMono(Response.class)
                .doOnNext(resp -> log.debug("Received: {}", resp))
                .doOnError(ex -> log.error("{}:{}", ex.getClass().getName(), ex.getMessage()));

        //then
        StepVerifier
                .create(responseMono)
                .verifyError(WebClientResponseException.BadRequest.class);
    }

    @Test
    void badRequestTest_entity() {
        //given
        int input = 4;

        //when
        Mono<ResponseEntity<Response>> responseEntityMono = webClient
                .get()
                .uri("/reactive-math/square/{input}/throw", input)
                .retrieve()
                .toEntity(Response.class)
                .doOnNext(resp -> log.debug("Received: {}", resp));

        //then
        StepVerifier
                .create(responseEntityMono)
                .verifyError(WebClientResponseException.BadRequest.class);
    }


}

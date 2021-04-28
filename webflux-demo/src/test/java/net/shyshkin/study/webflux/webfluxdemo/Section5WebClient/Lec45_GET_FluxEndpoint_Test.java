package net.shyshkin.study.webflux.webfluxdemo.Section5WebClient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.BaseTest;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class Lec45_GET_FluxEndpoint_Test extends BaseTest {

    @Autowired
    WebClient webClient;

    @Test
    @Disabled("too long for ci/cd")
    void multiplicationTableStream() {
        //given
        int input = 6;
        AtomicInteger counter = new AtomicInteger(1);

        //when
        Flux<Response> responseFlux = webClient
                .get()
                .uri("/reactive-math/table/{input}/stream", input)
                .retrieve()
                .bodyToFlux(Response.class)
                .doOnNext(resp -> log.debug("Receive: {}", resp));

        //then
        StepVerifier
                .create(responseFlux)
                .thenConsumeWhile(response -> response.getOutput() == counter.getAndIncrement() * input)
                .verifyComplete();
        assertThat(counter.get()).isEqualTo(11);
    }

    @Test
    @Disabled("too long for ci/cd")
    void multiplicationTableStream_entity() {
        //given
        int input = 6;
        AtomicInteger counter = new AtomicInteger(1);

        //when
        Mono<ResponseEntity<Flux<Response>>> responseEntityMono = webClient
                .get()
                .uri("/reactive-math/table/{input}/stream", input)
                .retrieve()
                .toEntityFlux(Response.class);

        //then
        Flux<Response> responseFlux = responseEntityMono
                .log("ResponseEntityMono")
                .flatMapMany(HttpEntity::getBody);

        StepVerifier
                .create(responseFlux.log())
                .thenConsumeWhile(response -> response.getOutput() == counter.getAndIncrement() * input)
                .verifyComplete();
        assertThat(counter.get()).isEqualTo(11);
    }

    @Test
    @Disabled("too long for ci/cd")
    void multiplicationTable_entityList() {
        //given
        int input = 6;
        AtomicInteger counter = new AtomicInteger(1);

        //when
        Mono<ResponseEntity<Flux<Response>>> responseEntityMono = webClient
                .get()
                .uri("/reactive-math/table/{input}", input)
                .retrieve()
                .toEntityFlux(Response.class);

        //then
        Flux<Response> responseFlux = responseEntityMono
                .log("ResponseEntityMono")
                .flatMapMany(HttpEntity::getBody);

        StepVerifier
                .create(responseFlux.log())
                .thenConsumeWhile(response -> response.getOutput() == counter.getAndIncrement() * input)
                .verifyComplete();
        assertThat(counter.get()).isEqualTo(11);
    }

}

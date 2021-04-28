package net.shyshkin.study.webflux.webfluxdemo.Section5WebClient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.BaseTest;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import net.shyshkin.study.webflux.webfluxdemo.dto.VinsValidationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class Lec50_ExchangeVsRetrieve_Test extends BaseTest {

    @Autowired
    WebClient webClient;

    @Test
    void exchange_correct() {
        //given
        int input = 12;

        //when
        Mono<Object> mono = webClient
                .get()
                .uri("/reactive-math/square/{input}/throw", input)
                .exchangeToMono(this::responseHandler)
                .doOnNext(resp -> log.debug("Received: {}", resp))
                .doOnError(ex -> log.error("{}:{}", ex.getClass().getName(), ex.getMessage()));

        //then
        StepVerifier
                .create(mono)
                .assertNext(o -> assertThat(o)
                        .isInstanceOf(Response.class)
                        .hasFieldOrPropertyWithValue("output", input * input))
                .verifyComplete();
    }

    @Test
    void exchange_errorHandle() {
        //given
        int input = 4;

        //when
        Mono<Object> mono = webClient
                .get()
                .uri("/reactive-math/square/{input}/throw", input)
                .exchangeToMono(this::responseHandler)
                .doOnNext(resp -> log.debug("Received: {}", resp))
                .doOnError(ex -> log.error("{}:{}", ex.getClass().getName(), ex.getMessage()));

        //then
        StepVerifier
                .create(mono)
                .assertNext(o -> assertThat(o)
                        .isInstanceOf(VinsValidationResponse.class)
                        .hasFieldOrPropertyWithValue("input", input)
                        .hasFieldOrPropertyWithValue("errorCode", 100)
                        .hasFieldOrPropertyWithValue("message", "allowed range from 10 to 20")
                )
                .verifyComplete();
    }

    private Mono<Object> responseHandler(ClientResponse clientResponse) {
        return clientResponse.rawStatusCode() == 400 ?
                clientResponse.bodyToMono(VinsValidationResponse.class) :
                clientResponse.bodyToMono(Response.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 12})
    void exchange_subscriber(int input) throws InterruptedException {

        //given
        CountDownLatch latch = new CountDownLatch(1);

        //when
        Mono<Object> mono = webClient
                .get()
                .uri("/reactive-math/square/{input}/throw", input)
                .exchangeToMono(this::responseHandler)
                .doOnNext(resp -> log.debug("Received: {}", resp))
                .doOnError(ex -> log.error("{}:{}", ex.getClass().getName(), ex.getMessage()));

        //then
        mono
                .subscribe(
                        o -> log.debug("In subscriber: {}", o),
                        ex -> log.error("{}:{}", ex.getClass().getName(), ex.getMessage()),
                        () -> {
                            log.debug("Completed");
                            latch.countDown();
                        }
                );
        latch.await(2, TimeUnit.SECONDS);
    }

}

package net.shyshkin.study.webflux.webfluxdemo.Section5WebClient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.BaseTest;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class Lec54_Assignment_Test extends BaseTest {

    @Autowired
    WebClient webClient;

    @Test
    void assignment() {
        //given
        Flux<Integer> secondOperandFlux = Flux.range(1, 5);
        Flux<String> operatorFlux = Flux.just("+", "-", "*", "/");

        //when
        Flux<Response> responseFlux = secondOperandFlux
                .flatMap(
                        secondOperand -> operatorFlux
                                .flatMap(
                                        operator -> oneOperation(secondOperand, operator)
                                )
                );

        //then
        StepVerifier
                .create(responseFlux)
                .expectNextCount(5 * 4)
                .verifyComplete();
    }

    private Mono<Response> oneOperation(int secondOperand, String operator) {
        return webClient
                .get()
                .uri("/calculator/10/{second}", secondOperand)
                .header("OP", operator)
                .retrieve()
                .bodyToMono(Response.class)
                .doOnNext(response -> log.debug("10 {} {} = {}", operator, secondOperand, response.getOutput()));
    }


}
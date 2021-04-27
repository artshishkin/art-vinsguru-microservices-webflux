package net.shyshkin.study.webflux.webfluxdemo.controller;

import net.shyshkin.study.webflux.webfluxdemo.dto.MultiplyRequestDto;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest
class ReactiveMathControllerTest {

    @Autowired
    ReactiveMathController controller;

    @Autowired
    WebTestClient webClient;

    @Test
    void findSquare() {
        //given
        int input = 6;

        //when
        webClient
                .get()
                .uri("/reactive-math/square/{input}", input)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(Response.class)
                .value(response -> assertThat(response.getOutput()).isEqualTo(input * input));
    }

    @Test
    @Disabled("too long for ci/cd")
    void multiplicationTable() {
        //given
        int input = 6;
        ParameterizedTypeReference<List<Response>> typeReference = new ParameterizedTypeReference<>() {
        };

        //when
        webClient
                .get()
                .uri("/reactive-math/table/{input}", input)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(typeReference)
                .value(list -> assertThat(list)
                        .hasSize(10)
                        .allMatch(response -> response.getOutput() % input == 0));
    }

    @Test
    @Disabled("too long for ci/cd")
    void multiplicationTableStream() {
        //given
        int input = 6;
        AtomicInteger counter = new AtomicInteger(1);

        //when
        Flux<Response> flux = webClient
                .get()
                .uri("/reactive-math/table/{input}/stream", input)
                .exchange()

                //then
                .expectStatus().isOk()
                .returnResult(Response.class)
                .getResponseBody();

        StepVerifier
                .create(flux.log())
                .thenConsumeWhile(response -> response.getOutput() == counter.getAndIncrement() * input)
                .verifyComplete();
        assertThat(counter.get()).isEqualTo(11);
    }

    @Test
    @Disabled("too long for ci/cd")
    void multiplicationTableStream_cancel() {
        //given
        int input = 6;
        AtomicInteger counter = new AtomicInteger(1);

        //when
        Flux<Response> flux = webClient
                .get()
                .uri("/reactive-math/table/{input}/stream", input)
                .exchange()

                //then
                .expectStatus().isOk()
                .returnResult(Response.class)
                .getResponseBody();

        StepVerifier
                .create(flux.take(3))
                .thenConsumeWhile(response -> response.getOutput() == counter.getAndIncrement() * input)
                .verifyComplete();
        assertThat(counter.get()).isEqualTo(4);
    }

    @Test
    void multiply() {
        //given
        int first = 4;
        int second = 5;
        MultiplyRequestDto multiplyRequestDto = new MultiplyRequestDto(first, second);

        //when
        webClient.post().uri("/reactive-math/multiply")
                .body(Mono.just(multiplyRequestDto), MultiplyRequestDto.class)
                .exchange()

                //then
                .expectStatus().isCreated()
                .expectBody(Response.class)
                .value(response -> assertThat(response.getOutput()).isEqualTo(first * second));
    }
}
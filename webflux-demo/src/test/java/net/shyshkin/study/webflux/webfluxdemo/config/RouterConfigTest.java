package net.shyshkin.study.webflux.webfluxdemo.config;

import net.shyshkin.study.webflux.webfluxdemo.dto.MultiplyRequestDto;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
class RouterConfigTest {

    @Autowired
    WebTestClient webClient;

    @ParameterizedTest
    @ValueSource(strings = {
            "/router/square/{input}",
            "/router-vins/square/{input}"
    })
    void serverResponseRouterFunction(String uri) {
        //given
        int input = 6;

        //when
        webClient
                .get()
                .uri(uri, input)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(Response.class)
                .value(response -> assertThat(response.getOutput()).isEqualTo(input * input));
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
                .uri("/router/table/{input}", input)
                .exchange()

                //then
                .expectStatus().isOk()
                .returnResult(Response.class)
                .getResponseBody();

        StepVerifier
                .create(flux)
                .thenConsumeWhile(response -> response.getOutput() == counter.getAndIncrement() * input)
                .verifyComplete();
        assertThat(counter.get()).isEqualTo(11);
    }

    @Test
    void multiply() {
        //given
        int first = 4;
        int second = 5;
        MultiplyRequestDto multiplyRequestDto = new MultiplyRequestDto(first, second);

        //when
        webClient.post().uri("/router/multiply")
                .body(Mono.just(multiplyRequestDto), MultiplyRequestDto.class)
                .exchange()

                //then
                .expectStatus().isCreated()
                .expectBody(Response.class)
                .value(response -> assertThat(response.getOutput()).isEqualTo(first * second));
    }
}
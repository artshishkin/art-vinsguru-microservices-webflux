package net.shyshkin.study.webflux.webfluxdemo.config;

import net.shyshkin.study.webflux.webfluxdemo.dto.MultiplyRequestDto;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import net.shyshkin.study.webflux.webfluxdemo.dto.VinsValidationResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@AutoConfigureWebTestClient
class RouterConfigTest {

    @Autowired
    WebTestClient webClient;

    @ParameterizedTest
    @ValueSource(strings = {
            "/router/square/{input}",
            "/router-vins/square/{input}",
            "/router/square/{input}/bad-request"
    })
    void findSquare_valid(String uri) {
        //given
        int input = 16;

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

    @ParameterizedTest
    @CsvSource({
            "/router/square/{input}/bad-request,6",
            "/router/square/{input}/bad-request,33",
            "/router/square/{input},6",
            "/router/square/{input},33",
            "/router-vins/square/{input},6",
            "/router-vins/square/{input},33"
    })
    void findSquare_invalid(String uri, int input) {
        //when
        webClient
                .get()
                .uri(uri, input)
                .exchange()

                //then
                .expectStatus().isBadRequest()
                .expectBody(VinsValidationResponse.class)
                .value(response ->
                        assertAll(
                                () -> assertThat(response).hasNoNullFieldsOrProperties()
                                        .hasFieldOrPropertyWithValue("errorCode", 100)
                                        .hasFieldOrPropertyWithValue("input", input)
                                        .hasFieldOrPropertyWithValue("message", "allowed range from 10 to 20"),
                                () -> assertThat(response.getTimestamp()).isBefore(LocalDateTime.now())
                        )
                );
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
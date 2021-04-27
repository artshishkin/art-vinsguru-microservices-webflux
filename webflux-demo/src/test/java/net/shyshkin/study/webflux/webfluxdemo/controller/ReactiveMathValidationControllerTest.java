package net.shyshkin.study.webflux.webfluxdemo.controller;

import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import net.shyshkin.study.webflux.webfluxdemo.dto.VinsValidationResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@AutoConfigureWebTestClient
class ReactiveMathValidationControllerTest {

    @Autowired
    WebTestClient webClient;

    @ParameterizedTest
    @ValueSource(strings = {
            "/reactive-math/square/{input}/throw",
            "/reactive-math/square/{input}/mono-error",
            "/reactive-math/square/{input}/mono-error-handle"
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
            "/reactive-math/square/{input}/throw,6",
            "/reactive-math/square/{input}/throw,33",
            "/reactive-math/square/{input}/mono-error,6",
            "/reactive-math/square/{input}/mono-error,33",
            "/reactive-math/square/{input}/mono-error-handle,6",
            "/reactive-math/square/{input}/mono-error-handle,33"
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

}
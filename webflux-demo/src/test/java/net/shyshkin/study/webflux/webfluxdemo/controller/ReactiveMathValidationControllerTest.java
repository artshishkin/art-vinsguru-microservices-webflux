package net.shyshkin.study.webflux.webfluxdemo.controller;

import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import net.shyshkin.study.webflux.webfluxdemo.dto.VinsValidationResponse;
import org.junit.jupiter.api.Test;
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

    @Test
    void findSquare_valid() {
        //given
        int input = 16;

        //when
        webClient
                .get()
                .uri("/reactive-math/square/{input}/throw", input)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(Response.class)
                .value(response -> assertThat(response.getOutput()).isEqualTo(input * input));
    }

    @Test
    void findSquare_invalid() {
        //given
        int input = 6;

        //when
        webClient
                .get()
                .uri("/reactive-math/square/{input}/throw", input)
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
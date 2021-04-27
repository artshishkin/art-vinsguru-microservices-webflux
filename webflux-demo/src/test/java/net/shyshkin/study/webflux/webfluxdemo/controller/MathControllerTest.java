package net.shyshkin.study.webflux.webfluxdemo.controller;

import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MathControllerTest {

    @Autowired
    MathController controller;

    WebTestClient webClient;

    @BeforeEach
    void setUp() {
        webClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void findSquare() {
        //given
        int input = 6;

        //when
        webClient
                .get()
                .uri("/math/square/{input}", input)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(Response.class)
                .value(response -> assertThat(response.getOutput()).isEqualTo(input * input));
    }

    @Test
    @Disabled("too long -> java.lang.IllegalStateException: Timeout on blocking read for 5000000000 NANOSECONDS")
    void multiplicationTable() {
        //given
        int input = 6;
        ParameterizedTypeReference<List<Response>> typeReference = new ParameterizedTypeReference<>() {
        };

        //when
        webClient
                .get()
                .uri("/math/table/{input}", input)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(typeReference)
                .value(list -> assertThat(list)
                        .hasSize(10)
                        .allMatch(response -> response.getOutput() % input == 0));
    }
}
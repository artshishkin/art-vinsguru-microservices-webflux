package net.shyshkin.study.webflux.webfluxdemo.config;

import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

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
}
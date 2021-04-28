package net.shyshkin.study.webflux.webfluxdemo.assignment.config;

import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
class AssignmentRouterConfigTest {

    @Autowired
    WebTestClient webClient;

    @ParameterizedTest(name = "[{index}] {arguments}")
    @CsvSource({
            "12,+,4,=,16",
            "12,-,4,=,8",
            "12,/,4,=,3",
            "12,*,4,=,48",
    })
    void assignmentRouter_headerPresent(int firstOperand, String operator, int secondOperand, String equalSign, int expectedResult) {

        //when
        webClient.get().uri("/calculator/{first}/{second}", firstOperand, secondOperand)
                .header("OP", operator)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(Response.class)
                .value(resp -> assertThat(resp.getOutput()).isEqualTo(expectedResult));
    }


    @Test
    void assignmentRouter_headerAbsent() {
        //given
        int firstOperand = 12;
        int secondOperand = 3;

        //when
        webClient.get().uri("/calculator/{first}/{second}", firstOperand, secondOperand)
                .exchange()

                //then
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(resp -> assertThat(resp).isEqualTo("operator header 'OP' must not be empty or null"));
    }

    @Test
    void assignmentRouter_wrongOperator() {
        //given
        int firstOperand = 12;
        int secondOperand = 3;
        String operator = "^";

        //when
        webClient.get().uri("/calculator/{first}/{second}", firstOperand, secondOperand)
                .header("OP", operator)
                .exchange()

                //then
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(resp -> assertThat(resp).isEqualTo("function for `^` not found"));
    }
}
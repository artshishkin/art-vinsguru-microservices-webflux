package net.shyshkin.study.webflux.webfluxdemo.assignment.config;

import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
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
            "calculator,12,+,4,=,16",
            "calculator,12,-,4,=,8",
            "calculator,12,/,4,=,3",
            "calculator,12,*,4,=,48",
            "calculator2,12,+,4,=,16",
            "calculator2,12,-,4,=,8",
            "calculator2,12,/,4,=,3",
            "calculator2,12,*,4,=,48",
    })
    void assignmentRouter_headerPresent(String endpoint, int firstOperand, String operator, int secondOperand, String equalSign, int expectedResult) {

        //when
        webClient.get().uri("/{endpoint}/{first}/{second}", endpoint, firstOperand, secondOperand)
                .header("OP", operator)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(Response.class)
                .value(resp -> assertThat(resp.getOutput()).isEqualTo(expectedResult));
    }


    @ParameterizedTest
    @ValueSource(strings = {"calculator", "calculator2"})
    void assignmentRouter_headerAbsent(String endpoint) {
        //given
        int firstOperand = 12;
        int secondOperand = 3;

        //when
        webClient.get().uri("/{endpoint}/{first}/{second}", endpoint, firstOperand, secondOperand)
                .exchange()

                //then
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(resp -> assertThat(resp).isEqualTo("operator header 'OP' must not be empty or null"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"calculator", "calculator2"})
    void assignmentRouter_wrongOperator(String endpoint) {
        //given
        int firstOperand = 12;
        int secondOperand = 3;
        String operator = "^";

        //when
        webClient.get().uri("/{endpoint}/{first}/{second}", endpoint, firstOperand, secondOperand)
                .header("OP", operator)
                .exchange()

                //then
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(resp -> assertThat(resp).isEqualTo("function for `^` not found"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"calculator", "calculator2"})
    void assignmentRouter_divisionByZero(String endpoint) {
        //given
        int firstOperand = 12;
        int secondOperand = 0;
        String operator = "/";

        //when
        webClient.get().uri("/{endpoint}/{first}/{second}", endpoint, firstOperand, secondOperand)
                .header("OP", operator)
                .exchange()

                //then
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(resp -> assertThat(resp).isEqualTo("/ by zero"));
    }

}
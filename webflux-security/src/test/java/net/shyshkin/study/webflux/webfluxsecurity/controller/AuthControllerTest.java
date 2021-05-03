package net.shyshkin.study.webflux.webfluxsecurity.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.CoreMatchers.containsString;

@SpringBootTest
@AutoConfigureWebTestClient
class AuthControllerTest {

    @Autowired
    WebTestClient webClient;

    @Test
    @WithUserDetails("any")
    void any_hasAccessToAnyHome() {

        //given
        String endpoint = "any";

        //when
        webClient.get().uri("/home/any")
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(expectedSuccessResponse(endpoint));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user", "admin"})
    @WithUserDetails("any")
    void any_hasNoAccessTo(String endpoint) {

        //when
        webClient.get().uri("/home/{role}", endpoint)
                .exchange()

                //then
                .expectStatus().isForbidden()
                .expectBody(String.class)
                .value(containsString("Denied"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user", "any"})
    @WithUserDetails("user")
    void user_hasAccessTo(String endpoint) {

        //when
        webClient.get().uri("/home/{role}", endpoint)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(expectedSuccessResponse(endpoint));
    }

    @Test
    @WithUserDetails("user")
    void user_hasNoAccessToAdminHome() {

        //when
        webClient.get().uri("/home/admin")
                .exchange()

                //then
                .expectStatus().isForbidden()
                .expectBody(String.class)
                .value(containsString("Denied"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "user", "any"})
    @WithUserDetails("admin")
    void admin_hasAccessTo(String endpoint) {

        //when
        webClient.get().uri("/home/{endpoint}", endpoint)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(expectedSuccessResponse(endpoint));
    }

    @ParameterizedTest
    @ValueSource(strings = {"admin", "user", "any"})
    @WithAnonymousUser
    void anonymous_hasNoAccessTo(String endpoint) {

        //when
        webClient.get().uri("/home/{endpoint}", endpoint)
                .exchange()

                //then
                .expectStatus().isForbidden()
                .expectBody(String.class)
                .value(containsString("Denied"));
    }

    private String expectedSuccessResponse(String user) {
        return "any".equals(user) ? "authenticated home" : user + " home";
    }
}
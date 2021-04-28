package net.shyshkin.study.webflux.webfluxdemo.Section5WebClient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.BaseTest;
import net.shyshkin.study.webflux.webfluxdemo.dto.MultiplyRequestDto;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class Lec52_SettingAuthToken_Test extends BaseTest {

    @Autowired
    WebClient webClient;

    @Test
    void authentication_basicAuth_inHeader_onEveryRequest() {
        //given
        int first = 4;
        int second = 5;
        MultiplyRequestDto multiplyRequestDto = new MultiplyRequestDto(first, second);

        //when
        Mono<ResponseEntity<Response>> responseEntityMono = webClient.post().uri("/reactive-math/multiply")
                .bodyValue(multiplyRequestDto)
                .headers(headers -> headers.setBasicAuth("username", "password"))
                .retrieve()
                .toEntity(Response.class)
                .doOnNext(entity -> log.debug("Receive entity: {}", entity));

        //then
        StepVerifier
                .create(responseEntityMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void authentication_basicAuth_viaWebClientConfiguration_fixedCredential() {
        //given
        int first = 4;
        int second = 5;
        MultiplyRequestDto multiplyRequestDto = new MultiplyRequestDto(first, second);

        WebClient webClientModified = this.webClient
                .mutate()
                .defaultHeaders(h -> h.setBasicAuth("username", "password"))
                .build();

        //when
        Mono<ResponseEntity<Response>> responseEntityMono = webClientModified.post().uri("/reactive-math/multiply")
                .bodyValue(multiplyRequestDto)
                .retrieve()
                .toEntity(Response.class)
                .doOnNext(entity -> log.debug("Receive entity: {}", entity));

        //then
        StepVerifier
                .create(responseEntityMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void authentication_bearer_viaWebClientConfiguration_modifiedCredentials() {
        //given
        int first = 4;
        int second = 5;
        MultiplyRequestDto multiplyRequestDto = new MultiplyRequestDto(first, second);

        WebClient webClientModified = this.webClient
                .mutate()
                .filter(this::sessionToken)
                .build();

        //when
        Mono<ResponseEntity<Response>> responseEntityMono = webClientModified.post().uri("/reactive-math/multiply")
                .bodyValue(multiplyRequestDto)
                .retrieve()
                .toEntity(Response.class)
                .doOnNext(entity -> log.debug("Receive entity: {}", entity));

        //then
        StepVerifier
                .create(responseEntityMono)
                .expectNextCount(1)
                .verifyComplete();
    }

    private Mono<ClientResponse> sessionToken(ClientRequest request, ExchangeFunction next) {
        log.debug("Generating session token");
        ClientRequest newClientRequest = ClientRequest
                .from(request)
                .headers(h -> h.setBearerAuth("some-lengthy-jwt-token"))
                .build();
        return next.exchange(newClientRequest);
    }
}
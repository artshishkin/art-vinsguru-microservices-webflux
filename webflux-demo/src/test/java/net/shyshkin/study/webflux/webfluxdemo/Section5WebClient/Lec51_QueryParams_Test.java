package net.shyshkin.study.webflux.webfluxdemo.Section5WebClient;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.webfluxdemo.BaseTest;
import net.shyshkin.study.webflux.webfluxdemo.dto.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class Lec51_QueryParams_Test extends BaseTest {

    @Autowired
    WebClient webClient;
    private String queryString = "http://localhost:8080/params?first={first}&second={second}";

    @Test
    void params_uriBuilder_queryParam() {

        //given
        int first = 3;
        int second = 4;

        //when
        Mono<Response> responseMono = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/params")
                        .queryParam("first", first)
                        .queryParam("second", second)
                        .build()
                )
                .retrieve()
                .bodyToMono(Response.class);

        //then
        StepVerifier
                .create(responseMono
                        .doOnNext(response -> log.debug("{}", response)))
                .assertNext(response -> assertThat(response.getOutput()).isEqualTo(first * second))
                .verifyComplete();
    }

    @Test
    void params_uriBuilder_query() {

        //given
        int first = 3;
        int second = 4;

        //when
        Mono<Response> responseMono = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/params")
                        .query("first={first}&second={second}")
                        .build(first, second)
                )
                .retrieve()
                .bodyToMono(Response.class);

        //then
        StepVerifier
                .create(responseMono
                        .doOnNext(response -> log.debug("{}", response)))
                .assertNext(response -> assertThat(response.getOutput()).isEqualTo(first * second))
                .verifyComplete();
    }

    @Test
    void params_uriBuilder_queryMap() {

        //given
        int first = 3;
        int second = 4;
        Map<String, Integer> params = Map.of(
                "first", first,
                "second", second
        );

        //when
        Mono<Response> responseMono = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/params")
                        .query("first={first}&second={second}")
                        .build(params)
                )
                .retrieve()
                .bodyToMono(Response.class);

        //then
        StepVerifier
                .create(responseMono
                        .doOnNext(response -> log.debug("{}", response)))
                .assertNext(response -> assertThat(response.getOutput()).isEqualTo(first * second))
                .verifyComplete();
    }

    @Test
    void params_uriBuilder_multiValueMap() {

        //given
        int first = 3;
        int second = 4;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("first", String.valueOf(first));
        params.add("second", String.valueOf(second));

        //when
        Mono<Response> responseMono = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/params")
                        .queryParams(params)
                        .build()
                )
                .retrieve()
                .bodyToMono(Response.class);

        //then
        StepVerifier
                .create(responseMono
                        .doOnNext(response -> log.debug("{}", response)))
                .assertNext(response -> assertThat(response.getOutput()).isEqualTo(first * second))
                .verifyComplete();
    }


    @Test
    void params_UriComponentsBuilder() {

        //given
        int first = 3;
        int second = 4;
        URI uri = UriComponentsBuilder
                .fromUriString(queryString)
                .build(first, second);

        //when
        Mono<Response> responseMono = webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Response.class);

        //then
        StepVerifier
                .create(responseMono
                        .doOnNext(response -> log.debug("{}", response)))
                .assertNext(response -> assertThat(response.getOutput()).isEqualTo(first * second))
                .verifyComplete();
    }
}

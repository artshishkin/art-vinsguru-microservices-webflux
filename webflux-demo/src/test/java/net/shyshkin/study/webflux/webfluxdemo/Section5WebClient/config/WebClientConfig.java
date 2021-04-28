package net.shyshkin.study.webflux.webfluxdemo.Section5WebClient.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient
                .builder()
                .baseUrl("http://localhost:8080")
                .filter(this::sessionToken)
                .build();
    }

    private Mono<ClientResponse> sessionToken(ClientRequest request, ExchangeFunction next) {

        Optional<Object> authOptional = request.attribute("auth");

        ClientRequest newClientRequest = authOptional
                .map(
                        authType -> "basic".equals(authType) ? withBasicAuth(request) :
                                "bearer".equals(authType) ? withBearerAuth(request) :
                                        request)
                .orElse(request);

        return next.exchange(newClientRequest);
    }

    private ClientRequest withBasicAuth(ClientRequest request) {
        return ClientRequest
                .from(request)
                .headers(h -> h.setBasicAuth("username", "password"))
                .build();
    }

    private ClientRequest withBearerAuth(ClientRequest request) {
        return ClientRequest
                .from(request)
                .headers(h -> h.setBearerAuth("some-token"))
                .build();
    }

}

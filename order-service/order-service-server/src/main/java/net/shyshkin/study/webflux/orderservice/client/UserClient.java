package net.shyshkin.study.webflux.orderservice.client;

import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;
import net.shyshkin.study.webflux.userservice.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserClient {

    private final WebClient webClient;

    public UserClient(WebClient webClient, @Value("${user-service.url}") String url) {
        this.webClient = webClient.mutate().baseUrl(url).build();
    }

    public Mono<TransactionResponseDto> createTransaction(TransactionRequestDto requestDto) {
        return webClient.post()
                .uri("transactions")
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class);
    }

    public Flux<UserDto> getAll() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(UserDto.class);
    }
}

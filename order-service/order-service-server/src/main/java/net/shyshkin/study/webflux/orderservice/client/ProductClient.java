package net.shyshkin.study.webflux.orderservice.client;

import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductClient {

    private final WebClient webClient;

    public ProductClient(WebClient webClient, @Value("${product-service.url}") String url) {
        this.webClient = webClient.mutate().baseUrl(url).build();
    }

    public Flux<ProductDto> getAll() {
        return webClient.get()
                .retrieve()
                .bodyToFlux(ProductDto.class);
    }

    public Mono<ProductDto> getProductById(String id) {
        return webClient.get()
                .uri("{productId}", id)
                .retrieve()
                .bodyToMono(ProductDto.class);
    }
}

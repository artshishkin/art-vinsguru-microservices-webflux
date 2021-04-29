package net.shyshkin.study.webflux.productservice.service;

import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Flux<ProductDto> getAll();

    Mono<ProductDto> getProductById(String id);

    Mono<ProductDto> insertProduct(Mono<ProductDto> productDtoMono);

    Mono<ProductDto> updateProduct(String id, Mono<ProductDto> productDtoMono);

    Mono<Void> deleteProduct(String id);

}

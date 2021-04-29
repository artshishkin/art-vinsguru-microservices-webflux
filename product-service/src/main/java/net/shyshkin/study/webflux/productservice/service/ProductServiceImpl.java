package net.shyshkin.study.webflux.productservice.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import net.shyshkin.study.webflux.productservice.mapper.ProductMapper;
import net.shyshkin.study.webflux.productservice.repository.ProductRepository;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public Flux<ProductDto> getAll() {
        return repository
                .findAll()
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductDto> getProductById(String id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductDto> insertProduct(Mono<ProductDto> productDtoMono) {
        return productDtoMono
                .map(mapper::toProduct)
                .doOnNext(product -> product.setId(null))
                .flatMap(repository::insert)
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductDto> updateProduct(String id, Mono<ProductDto> productDtoMono) {
        return repository
                .findById(id)
                .flatMap(product -> productDtoMono)
                .map(mapper::toProduct)
                .doOnNext(product -> product.setId(id))
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteProduct(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<ProductDto> getProductsByPriceInRange(int min, int max) {
        return repository.findByPriceInRange(min, max)
                .map(mapper::toDto);
    }

    @Override
    public Flux<ProductDto> getProductsByPriceInRange(Range<Integer> range) {
        return repository.findByPriceBetween(range)
                .map(mapper::toDto);
    }
}

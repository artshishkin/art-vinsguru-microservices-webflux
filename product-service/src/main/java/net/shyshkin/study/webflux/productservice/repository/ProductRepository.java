package net.shyshkin.study.webflux.productservice.repository;

import net.shyshkin.study.webflux.productservice.entity.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}

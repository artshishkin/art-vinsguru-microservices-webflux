package net.shyshkin.study.webflux.productservice.repository;

import net.shyshkin.study.webflux.productservice.entity.Product;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

    @Query("{'price' : { $gte: ?0, $lte: ?1 } }")
    Flux<Product> findByPriceInRange(int min, int max);

}

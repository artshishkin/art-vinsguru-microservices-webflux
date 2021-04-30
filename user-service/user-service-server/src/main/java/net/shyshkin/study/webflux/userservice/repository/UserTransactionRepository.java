package net.shyshkin.study.webflux.userservice.repository;

import net.shyshkin.study.webflux.userservice.entity.UserTransaction;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;

public interface UserTransactionRepository extends ReactiveSortingRepository<UserTransaction, Integer> {

    Flux<UserTransaction> findByUserId(Integer userId);
}

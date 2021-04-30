package net.shyshkin.study.webflux.userservice.repository;

import net.shyshkin.study.webflux.userservice.entity.User;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveSortingRepository<User, Integer> {

    @Modifying
    @Query(
            value = "update users " +
                    "set balance = balance - :amount " +
                    "where id = :userId and balance >= :amount"
    )
    Mono<Boolean> updateUserBalance(Integer userId, Integer amount);
}

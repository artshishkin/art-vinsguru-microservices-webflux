package net.shyshkin.study.webflux.userservice.repository;

import net.shyshkin.study.webflux.userservice.entity.User;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface UserRepository extends ReactiveSortingRepository<User, Integer> {
}

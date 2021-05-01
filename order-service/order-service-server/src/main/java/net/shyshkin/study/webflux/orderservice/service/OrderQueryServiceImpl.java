package net.shyshkin.study.webflux.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.repository.PurchaseOrderRepository;
import net.shyshkin.study.webflux.orderservice.util.EntityDtoUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryServiceImpl implements OrderQueryService {

    private final PurchaseOrderRepository repository;

    @Override
    public Flux<PurchaseOrderResponseDto> getOrdersByUserId(Integer userId) {
        return Mono
                .fromSupplier(() -> repository.findAllByUserId(userId))
                .doOnNext(orderList -> log.debug("orderList: {}", orderList))
                .flatMapMany(Flux::fromIterable)
                .map(EntityDtoUtil::toDto)
                .subscribeOn(Schedulers.boundedElastic());
    }
}

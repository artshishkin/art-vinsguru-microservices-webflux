package net.shyshkin.study.webflux.orderservice.service;

import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import reactor.core.publisher.Flux;

public interface OrderQueryService {

    Flux<PurchaseOrderResponseDto> getOrdersByUserId(Integer userId);

}

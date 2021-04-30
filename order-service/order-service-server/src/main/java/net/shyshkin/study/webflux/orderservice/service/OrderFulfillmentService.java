package net.shyshkin.study.webflux.orderservice.service;

import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderRequestDto;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import reactor.core.publisher.Mono;

public interface OrderFulfillmentService {

    Mono<PurchaseOrderResponseDto> processOrder(Mono<PurchaseOrderRequestDto> requestDtoMono);

}

package net.shyshkin.study.webflux.orderservice.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderRequestDto;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.service.OrderFulfillmentService;
import net.shyshkin.study.webflux.orderservice.service.OrderQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final OrderFulfillmentService orderFulfillmentService;
    private final OrderQueryService orderQueryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PurchaseOrderResponseDto> purchaseOrder(@RequestBody Mono<PurchaseOrderRequestDto> requestDtoMono) {
        return orderFulfillmentService.processOrder(requestDtoMono);
    }

    @GetMapping(value = "users/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<PurchaseOrderResponseDto> getOrdersByUserId(@PathVariable Integer userId) {
        return orderQueryService.getOrdersByUserId(userId);
    }
}

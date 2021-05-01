package net.shyshkin.study.webflux.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webflux.orderservice.client.ProductClient;
import net.shyshkin.study.webflux.orderservice.client.UserClient;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderRequestDto;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.dto.RequestContext;
import net.shyshkin.study.webflux.orderservice.repository.PurchaseOrderRepository;
import net.shyshkin.study.webflux.orderservice.util.EntityDtoUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderFulfillmentServiceImpl implements OrderFulfillmentService {

    private final UserClient userClient;
    private final ProductClient productClient;
    private final PurchaseOrderRepository orderRepository;

    @Override
    public Mono<PurchaseOrderResponseDto> processOrder(Mono<PurchaseOrderRequestDto> requestDtoMono) {
        return requestDtoMono
                .map(RequestContext::new)
                .flatMap(this::productRequestResponse)
                .doOnNext(EntityDtoUtil::setTransactionDtoUtil)
                .flatMap(this::userRequestResponse)
                .map(EntityDtoUtil::getPurchaseOrder)
                .map(orderRepository::save) //blocking
                .doOnNext(purchaseOrder -> log.debug("Saved {}", purchaseOrder))
                .map(EntityDtoUtil::toDto)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<RequestContext> productRequestResponse(RequestContext rc) {
        return productClient
                .getProductById(rc.getPurchaseOrderRequestDto().getProductId())
                .doOnNext(rc::setProductDto)
                .thenReturn(rc);
    }

    private Mono<RequestContext> userRequestResponse(RequestContext rc) {
        return userClient
                .createTransaction(rc.getTransactionRequestDto())
                .doOnNext(rc::setTransactionResponseDto)
                .thenReturn(rc);
    }

}

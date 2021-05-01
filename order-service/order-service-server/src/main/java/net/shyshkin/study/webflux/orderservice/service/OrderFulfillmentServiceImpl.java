package net.shyshkin.study.webflux.orderservice.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webflux.orderservice.client.ProductClient;
import net.shyshkin.study.webflux.orderservice.client.UserClient;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderRequestDto;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.dto.RequestContext;
import net.shyshkin.study.webflux.orderservice.util.EntityDtoUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderFulfillmentServiceImpl implements OrderFulfillmentService {

    private final UserClient userClient;
    private final ProductClient productClient;

    @Override
    public Mono<PurchaseOrderResponseDto> processOrder(Mono<PurchaseOrderRequestDto> requestDtoMono) {
        requestDtoMono
                .map(RequestContext::new)
                .flatMap(this::productRequestResponse)
                .doOnNext(EntityDtoUtil::setTransactionDtoUtil)
                .flatMap(this::userRequestResponse);
        return null;
    }

    private Mono<RequestContext> productRequestResponse(RequestContext rc) {
        return productClient
                .getProductById(rc.getProductDto().getId())
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

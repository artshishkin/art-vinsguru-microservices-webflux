package net.shyshkin.study.webflux.orderservice.util;

import net.shyshkin.study.webflux.orderservice.dto.OrderStatus;
import net.shyshkin.study.webflux.orderservice.dto.PurchaseOrderResponseDto;
import net.shyshkin.study.webflux.orderservice.dto.RequestContext;
import net.shyshkin.study.webflux.orderservice.entity.PurchaseOrder;
import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionStatus;

public class EntityDtoUtil {

    public static void setTransactionDtoUtil(RequestContext rc) {
        TransactionRequestDto transactionRequestDto = TransactionRequestDto
                .builder()
                .amount(rc.getProductDto().getPrice())
                .userId(rc.getPurchaseOrderRequestDto().getUserId())
                .build();
        rc.setTransactionRequestDto(transactionRequestDto);
    }

    public static PurchaseOrder getPurchaseOrder(RequestContext rc) {

        TransactionStatus transactionStatus = rc.getTransactionResponseDto().getStatus();

        return PurchaseOrder.builder()
                .amount(rc.getTransactionResponseDto().getAmount())
                .productId(rc.getProductDto().getId())
                .userId(rc.getTransactionResponseDto().getUserId())
                .status(toOrderStatus(transactionStatus))
                .build();
    }

    private static OrderStatus toOrderStatus(TransactionStatus status) {
        switch (status) {
            case APPROVED:
                return OrderStatus.COMPLETED;
            case DECLINED:
                return OrderStatus.FAILED;
        }
        throw new IllegalStateException("Transaction Status Unknown: " + status);
    }

    public static PurchaseOrderResponseDto toDto(PurchaseOrder order) {

        return PurchaseOrderResponseDto.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .amount(order.getAmount())
                .status(order.getStatus())
                .build();
    }
}

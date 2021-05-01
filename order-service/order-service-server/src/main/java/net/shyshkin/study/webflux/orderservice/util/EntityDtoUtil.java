package net.shyshkin.study.webflux.orderservice.util;

import net.shyshkin.study.webflux.orderservice.dto.RequestContext;
import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;

public class EntityDtoUtil {

    public static void setTransactionDtoUtil(RequestContext rc) {
        TransactionRequestDto transactionRequestDto = TransactionRequestDto
                .builder()
                .amount(rc.getProductDto().getPrice())
                .userId(rc.getPurchaseOrderRequestDto().getUserId())
                .build();
        rc.setTransactionRequestDto(transactionRequestDto);
    }
}

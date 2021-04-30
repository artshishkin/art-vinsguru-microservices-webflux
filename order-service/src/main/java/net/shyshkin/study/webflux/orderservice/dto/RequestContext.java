package net.shyshkin.study.webflux.orderservice.dto;

import lombok.Data;
import net.shyshkin.study.webflux.orderservice.dto.productservice.ProductDto;
import net.shyshkin.study.webflux.orderservice.dto.userservice.TransactionRequestDto;
import net.shyshkin.study.webflux.orderservice.dto.userservice.TransactionResponseDto;

@Data
public class RequestContext {
    private final PurchaseOrderRequestDto purchaseOrderRequestDto;
    private ProductDto productDto;
    private TransactionRequestDto transactionRequestDto;
    private TransactionResponseDto transactionResponseDto;
}

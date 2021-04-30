package net.shyshkin.study.webflux.orderservice.dto;

import lombok.Data;
import net.shyshkin.study.webflux.productservice.dto.ProductDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionRequestDto;
import net.shyshkin.study.webflux.userservice.dto.TransactionResponseDto;

@Data
public class RequestContext {
    private final PurchaseOrderRequestDto purchaseOrderRequestDto;
    private ProductDto productDto;
    private TransactionRequestDto transactionRequestDto;
    private TransactionResponseDto transactionResponseDto;
}

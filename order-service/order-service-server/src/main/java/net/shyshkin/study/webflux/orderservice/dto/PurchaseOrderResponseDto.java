package net.shyshkin.study.webflux.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderResponseDto {

    private Integer orderId;
    private Integer userId;
    private String productId;
    private Integer amount;
    private OrderStatus status;

}

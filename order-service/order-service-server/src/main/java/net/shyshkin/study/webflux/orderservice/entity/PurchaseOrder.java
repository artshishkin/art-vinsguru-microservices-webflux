package net.shyshkin.study.webflux.orderservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.shyshkin.study.webflux.orderservice.dto.OrderStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PurchaseOrder {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Integer id;

    private String productId;
    private Integer userId;

    private Integer amount;
    private OrderStatus status;
}

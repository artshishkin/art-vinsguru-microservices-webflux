package net.shyshkin.study.webflux.orderservice.repository;

import net.shyshkin.study.webflux.orderservice.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {

    List<PurchaseOrder> findAllByUserId(Integer userId);

}

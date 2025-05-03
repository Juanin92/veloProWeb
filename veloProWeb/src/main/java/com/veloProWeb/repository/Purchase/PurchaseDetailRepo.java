package com.veloProWeb.repository.Purchase;

import com.veloProWeb.model.entity.Purchase.PurchaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseDetailRepo extends JpaRepository<PurchaseDetail, Long> {
    List<PurchaseDetail> findByPurchaseId(Long idPurchase);
}

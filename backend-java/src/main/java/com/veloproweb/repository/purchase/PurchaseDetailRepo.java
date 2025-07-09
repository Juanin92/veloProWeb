package com.veloproweb.repository.purchase;

import com.veloproweb.model.entity.purchase.PurchaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseDetailRepo extends JpaRepository<PurchaseDetail, Long> {
    List<PurchaseDetail> findByPurchaseId(Long idPurchase);
}

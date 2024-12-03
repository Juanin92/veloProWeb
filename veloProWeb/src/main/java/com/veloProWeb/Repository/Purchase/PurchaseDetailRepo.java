package com.veloProWeb.Repository.Purchase;

import com.veloProWeb.Model.Entity.Purchase.PurchaseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseDetailRepo extends JpaRepository<PurchaseDetail, Long> {
}

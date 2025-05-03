package com.veloProWeb.repository.Sale;

import com.veloProWeb.model.entity.Sale.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleDetailRepo extends JpaRepository<SaleDetail, Long> {
    List<SaleDetail> findBySaleId(Long id);
    List<SaleDetail> findByDispatchId(Long idDispatch);
}

package com.veloproweb.repository.sale;

import com.veloproweb.model.entity.sale.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaleRepo extends JpaRepository<Sale, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM sale ORDER BY date DESC, id DESC LIMIT 1")
    Optional<Sale> findLastCreated();
}

package com.veloproweb.repository;

import com.veloproweb.model.entity.inventory.Kardex;
import com.veloproweb.model.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KardexRepo extends JpaRepository<Kardex, Long> {
    List<Kardex> findByProductAndDateAfter(Product product, LocalDate days);
}

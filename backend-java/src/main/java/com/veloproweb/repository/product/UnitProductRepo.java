package com.veloproweb.repository.product;

import com.veloproweb.model.entity.product.UnitProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitProductRepo extends JpaRepository<UnitProduct, Long> {
    Optional<UnitProduct> findByNameUnit(String name);

    @Query("SELECT u FROM UnitProduct u ORDER BY LOWER(u.nameUnit) ASC")
    List<UnitProduct> findAllOrderByNameAsc();
}

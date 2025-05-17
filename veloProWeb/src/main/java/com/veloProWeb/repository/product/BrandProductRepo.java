package com.veloProWeb.repository.product;

import com.veloProWeb.model.entity.product.BrandProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandProductRepo extends JpaRepository<BrandProduct, Long> {

    Optional<BrandProduct> findByName(String name);

    @Query("SELECT b FROM BrandProduct b ORDER BY LOWER(b.name) ASC")
    List<BrandProduct> findAllOrderByNameAsc();
}

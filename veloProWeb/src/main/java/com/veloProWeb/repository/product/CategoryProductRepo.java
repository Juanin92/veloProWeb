package com.veloProWeb.repository.product;

import com.veloProWeb.model.entity.product.CategoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryProductRepo extends JpaRepository<CategoryProduct, Long> {
    Optional<CategoryProduct> findByName(String name);

    @Query("SELECT c FROM CategoryProduct c ORDER BY LOWER(c.name) ASC")
    List<CategoryProduct> findAllOrderByNameAsc();
}

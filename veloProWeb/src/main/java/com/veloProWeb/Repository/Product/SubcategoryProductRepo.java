package com.veloProWeb.Repository.Product;

import com.veloProWeb.Model.Entity.Product.SubcategoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoryProductRepo extends JpaRepository<SubcategoryProduct, Long> {
    Optional<SubcategoryProduct> findByNameAndCategoryId(String name, Long id);
    List<SubcategoryProduct> findByCategoryId(Long id);
}

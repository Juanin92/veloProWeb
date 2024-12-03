package com.veloProWeb.Repository.Product;

import com.veloProWeb.Model.Entity.Product.BrandProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandProductRepo extends JpaRepository<BrandProduct, Long> {

    Optional<BrandProduct> findByName(String name);
}

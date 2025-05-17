package com.veloProWeb.repository.product;

import com.veloProWeb.model.entity.product.Product;
import io.micrometer.common.lang.NonNullApi;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@NonNullApi
@Repository
public interface ProductRepo extends JpaRepository<Product,Long> {

    Optional<Product> findById(@Nonnull Long id);
}

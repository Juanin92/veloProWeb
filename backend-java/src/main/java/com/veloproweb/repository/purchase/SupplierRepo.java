package com.veloproweb.repository.purchase;

import com.veloproweb.model.entity.purchase.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepo extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByRut(String rut);
}

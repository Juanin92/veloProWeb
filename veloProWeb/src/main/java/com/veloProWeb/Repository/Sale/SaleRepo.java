package com.veloProWeb.Repository.Sale;

import com.veloProWeb.Model.Entity.Sale.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepo extends JpaRepository<Sale, Long> {

}

package com.veloProWeb.Repository.Sale;

import com.veloProWeb.Model.Entity.Sale.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface SaleRepo extends JpaRepository<Sale, Long> {

}

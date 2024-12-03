package com.veloProWeb.Repository.Sale;

import com.veloProWeb.Model.Entity.Sale.CashRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CashRegisterRepo extends JpaRepository<CashRegister, Long> {

    @Query(value = "SELECT * FROM cash_register WHERE id_user = :userId AND status = 'OPEN' ORDER BY date_opening DESC LIMIT 1", nativeQuery = true)
    CashRegister findLatestOpenRegisterByUser(@Param("userId") Long userId);
}

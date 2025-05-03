package com.veloProWeb.repository.Sale;

import com.veloProWeb.model.entity.Sale.CashRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CashRegisterRepo extends JpaRepository<CashRegister, Long> {

    @Query(value = "SELECT * FROM cash_register WHERE id_user = :userId AND status = 'OPEN' " +
            "ORDER BY date_opening DESC LIMIT 1", nativeQuery = true)
    CashRegister findLatestOpenRegisterByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) > 0 FROM CashRegister c WHERE c.user.id = :userId AND " +
            "FUNCTION('DATE', c.dateOpening) = :dateOpening AND c.status = 'OPEN' AND c.dateClosing IS NULL")
    boolean existsOpenRegisterByUserAndDate(@Param("userId") Long userId, @Param("dateOpening") LocalDate dateOpening);
}

package com.veloProWeb.Repository;

import com.veloProWeb.Model.Entity.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KardexRepo extends JpaRepository<Kardex, Long> {
}

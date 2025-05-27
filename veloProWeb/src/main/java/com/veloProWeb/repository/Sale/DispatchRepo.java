package com.veloProWeb.repository.Sale;

import com.veloProWeb.model.entity.Sale.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispatchRepo extends JpaRepository<Dispatch, Long> {
}

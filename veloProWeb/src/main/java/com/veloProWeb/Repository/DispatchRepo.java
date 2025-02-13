package com.veloProWeb.Repository;

import com.veloProWeb.Model.Entity.Sale.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispatchRepo extends JpaRepository<Dispatch, Long> {
}

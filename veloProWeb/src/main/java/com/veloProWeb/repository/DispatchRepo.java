package com.veloProWeb.repository;

import com.veloProWeb.model.entity.Sale.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispatchRepo extends JpaRepository<Dispatch, Long> {
}

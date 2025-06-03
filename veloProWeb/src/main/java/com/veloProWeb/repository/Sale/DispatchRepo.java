package com.veloProWeb.repository.Sale;

import com.veloProWeb.model.Enum.DispatchStatus;
import com.veloProWeb.model.entity.Sale.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispatchRepo extends JpaRepository<Dispatch, Long> {
    List<Dispatch> findByStatusNot(DispatchStatus status);
}

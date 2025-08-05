package com.veloproweb.repository.sale;

import com.veloproweb.model.enums.DispatchStatus;
import com.veloproweb.model.entity.sale.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispatchRepo extends JpaRepository<Dispatch, Long> {
    List<Dispatch> findByStatusNot(DispatchStatus status);
}

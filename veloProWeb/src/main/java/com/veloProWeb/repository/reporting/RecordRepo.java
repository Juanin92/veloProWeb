package com.veloProWeb.repository.reporting;

import com.veloProWeb.model.entity.reporting.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepo extends JpaRepository<Record, Long> {
}

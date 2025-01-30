package com.veloProWeb.Repository;

import com.veloProWeb.Model.Entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepo extends JpaRepository<Record, Long> {
}

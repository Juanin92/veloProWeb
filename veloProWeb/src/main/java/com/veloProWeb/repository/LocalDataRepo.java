package com.veloProWeb.repository;

import com.veloProWeb.model.entity.data.LocalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalDataRepo extends JpaRepository<LocalData, Long> {
}

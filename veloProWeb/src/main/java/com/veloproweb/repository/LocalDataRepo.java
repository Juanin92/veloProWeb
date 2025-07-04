package com.veloproweb.repository;

import com.veloproweb.model.entity.data.LocalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalDataRepo extends JpaRepository<LocalData, Long> {
}

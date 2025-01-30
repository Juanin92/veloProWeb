package com.veloProWeb.Repository;

import com.veloProWeb.Model.Entity.LocalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalDataRepo extends JpaRepository<LocalData, Long> {
}

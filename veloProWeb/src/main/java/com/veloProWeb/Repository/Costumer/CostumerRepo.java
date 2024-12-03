package com.veloProWeb.Repository.Costumer;

import com.veloProWeb.Model.Entity.Costumer.Costumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CostumerRepo extends JpaRepository<Costumer,Long> {
    Optional<Costumer> findByNameAndSurname(String name, String surname);
}

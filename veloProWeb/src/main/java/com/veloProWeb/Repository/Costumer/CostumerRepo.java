package com.veloProWeb.Repository.Costumer;

import com.veloProWeb.Model.Entity.Costumer.Costumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CostumerRepo extends JpaRepository<Costumer,Long> {

    @Query(value = "SELECT * FROM costumer c WHERE c.surname = :surname AND (c.name LIKE CONCAT('%', :name, '%') OR :name LIKE CONCAT('%', c.name, '%'))", nativeQuery = true)
    Optional<Costumer> findBySimilarNameAndSurname(@Param("name") String name, @Param("surname") String surname);
    Optional<Costumer> findByNameAndSurname(String name, String surname);
}

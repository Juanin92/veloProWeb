package com.veloProWeb.repository.customer;

import com.veloProWeb.model.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer,Long> {

    @Query(value = "SELECT * FROM customer c WHERE c.surname = :surname AND (c.name LIKE CONCAT('%', :name, '%') OR :name LIKE CONCAT('%', c.name, '%'))", nativeQuery = true)
    Optional<Customer> findBySimilarNameAndSurname(@Param("name") String name, @Param("surname") String surname);
    Optional<Customer> findByNameAndSurname(String name, String surname);
}

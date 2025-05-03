package com.veloProWeb.repository;

import com.veloProWeb.model.entity.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUsernameAndToken(String username, String token);
    Optional<User> findByUsername(String username);
    Optional<User> findByRut(String rut);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByTokenIsNotNull();
}

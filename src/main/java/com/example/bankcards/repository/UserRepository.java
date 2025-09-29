package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    default Optional<User> findUserByUsername(String username) {
        return findByUsername(username);
    }

    default boolean existsUserByEmail(String email) {
        return existsByEmail(email);
    }

    default boolean existsUserByUsername(String username) {
        return existsByUsername(username);
    }

}
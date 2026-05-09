package com.example.Endurance.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsUserEntiesByName(String name);

    Optional<UserEntity> findByName(String name);
}

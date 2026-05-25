package com.example.Endurance.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    boolean existsUserEntiesByName(String name);

    Optional<UserEntity> findByName(String name);

    boolean existsUserEntiesByEmail(@NotBlank @Size(min = 2, max = 50) String email);
}

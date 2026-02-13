package com.placideh.rateLimiter.repository;


import com.placideh.rateLimiter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByTierId(String tierId);

    Optional<User> findByApiKey(String apiKey);

    boolean existsByUsername(String username);

    @Query("SELECT c FROM User c WHERE c.tierId = :tierId AND c.isActive = :isActive")
    List<User> findByTierIdAndIsActive(String tierId, boolean isActive);

    boolean existsByEmail(String email);
}

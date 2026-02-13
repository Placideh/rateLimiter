package com.placideh.rateLimiter.repository;

import com.placideh.rateLimiter.model.Tier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TierRepository extends JpaRepository<Tier, String> {

    Optional<Tier> findByName(String name);

    List<Tier> findByIsActive(Boolean isActive);

    boolean existsByName(String name);
}

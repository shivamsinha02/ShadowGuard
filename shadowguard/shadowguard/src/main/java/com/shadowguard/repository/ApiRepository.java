package com.shadowguard.repository;

import com.shadowguard.entity.Api;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.util.List;

public interface ApiRepository extends JpaRepository<Api, Long> {

    boolean existsByMethodAndEndpoint(String method, String endpoint);

    Optional<Api> findByMethodAndEndpoint(String method, String endpoint);

    List<Api> findByRiskLevelIn(List<String> riskLevels);
}
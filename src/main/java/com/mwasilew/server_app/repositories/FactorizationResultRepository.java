package com.mwasilew.server_app.repositories;

import com.mwasilew.server_app.models.FactorizationResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactorizationResultRepository extends JpaRepository<FactorizationResult, Integer> {
}

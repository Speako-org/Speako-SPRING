package com.speako.domain.analysis.repository;

import com.speako.domain.analysis.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
}


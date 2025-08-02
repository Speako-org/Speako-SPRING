package com.speako.domain.achievement.repository;

import com.speako.domain.achievement.domain.MonthlyStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyStatRepository extends JpaRepository<MonthlyStat, Integer> {
}

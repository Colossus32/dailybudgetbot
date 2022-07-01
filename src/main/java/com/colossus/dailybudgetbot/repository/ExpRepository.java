package com.colossus.dailybudgetbot.repository;

import com.colossus.dailybudgetbot.entity.DailyExp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpRepository extends JpaRepository<DailyExp,Long>, CustomRepository {
}

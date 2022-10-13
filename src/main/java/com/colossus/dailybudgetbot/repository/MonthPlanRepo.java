package com.colossus.dailybudgetbot.repository;

import com.colossus.dailybudgetbot.entity.MonthPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface MonthPlanRepo extends JpaRepository<MonthPlan,Long> {
    MonthPlan getByDate(Date date);
}

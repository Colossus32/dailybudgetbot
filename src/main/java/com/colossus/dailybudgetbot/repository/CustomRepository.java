package com.colossus.dailybudgetbot.repository;

import com.colossus.dailybudgetbot.entity.DailyExp;

import java.util.List;

public interface CustomRepository {
    List<DailyExp> findByMonthAndYear(int month, int year);
    List<DailyExp> findByDate(int day, int month, int year);
}

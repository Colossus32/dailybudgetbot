package com.colossus.dailybudgetbot.service;

import com.colossus.dailybudgetbot.entity.DailyExp;

import java.util.List;

public interface ExpService {
    DailyExp addExp(String num);
    String planForRestOfMonth();
    void deleteToday();
    String showExpsForTheMonth();
    String showPreviousMonthBalance();

    DailyExp saveExp(DailyExp dailyExp);
    List<DailyExp> findByMonthAndYear(int month, int year);
    List<DailyExp> findByDate(int day, int month, int year);
    void deleteById (long id);
    List<DailyExp> findAll();

}

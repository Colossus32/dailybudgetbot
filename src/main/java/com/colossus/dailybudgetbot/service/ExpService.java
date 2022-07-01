package com.colossus.dailybudgetbot.service;

import com.colossus.dailybudgetbot.entity.DailyExp;

public interface ExpService {
    DailyExp addExp(String num);
    String planForRestOfMonth();
    void deleteToday();
    String showExpsForTheMonth();
}

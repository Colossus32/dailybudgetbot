package com.colossus.dailybudgetbot.service;

import com.colossus.dailybudgetbot.entity.DailyExp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExpServiceTest {
    Calendar calendar = Calendar.getInstance();

    private final double COSTS = 1000.0;
    private final int day = calendar.getTime().getDate();
    private final int month = calendar.getTime().getMonth();
    private final int year = calendar.getTime().getYear() + 1900;

    @Autowired
    ExpService service;

    @AfterEach
    void tearDown() {
    }

    @Test
    void addExp() {
        DailyExp toCheck = service.addExp(String.valueOf(COSTS));
        assertEquals(COSTS,toCheck.getCost());
        assertEquals(day,toCheck.getDay());
        assertEquals(month,toCheck.getMonth());
        assertEquals(year, toCheck.getYear());
    }

    @Test
    void showExpsForTheMonth() {
        String fromDB = service.showExpsForTheMonth();
        assertEquals("05.06  |  "+COSTS+"\n",fromDB);
    }
}
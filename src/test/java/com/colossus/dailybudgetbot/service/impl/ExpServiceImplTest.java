package com.colossus.dailybudgetbot.service.impl;

import com.colossus.dailybudgetbot.entity.DailyExp;
import com.colossus.dailybudgetbot.service.ExpService;
import com.colossus.dailybudgetbot.util.HelpfulUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExpServiceImplTest {

    Calendar calendar = Calendar.getInstance();

    private final double COSTS = 1000.0;
    private final int DAY = calendar.getTime().getDate();
    private final int MONTH = calendar.getTime().getMonth();
    private final int YEAR = calendar.getTime().getYear() + 1900;
    @Value("${planExp}")
    private double PLAN;

    @Autowired
    ExpService service;

    @AfterEach
    void tearDown() {
        List<DailyExp> list = service.findAll();
        if (list.size()>0){
            for (DailyExp d: list) {
                service.deleteById(d.getId());
            }
        }
    }

    @Test
    void should_add_cost_to_daily_exp() {
        DailyExp toSave = createSimpleExp();
        service.saveExp(toSave);
        DailyExp fromDB = service.findAll().get(0);

        assertEquals(toSave.getId(), fromDB.getId());
        assertEquals(toSave.getCost(), fromDB.getCost());
        assertEquals(toSave.getDay(), fromDB.getDay());
        assertEquals(toSave.getMonth(), fromDB.getMonth());
        assertEquals(toSave.getYear(), fromDB.getYear());
    }

    @Test
    void should_give_rest_of_the_month() {
        service.saveExp(createSimpleExp());
        List<Integer> calendar = HelpfulUtils.getTodayDate();
        double sum = service.findByMonthAndYear(MONTH,YEAR).stream()
                .mapToDouble(DailyExp::getCost).sum();

        String forCheck = service.planForRestOfMonth();
        String target = String.format("%.2f", (PLAN - sum) / calendar.get(3)) + " RUB";

        assertEquals(target,forCheck);
    }

    @Test
    void should_delete_exp_for_today() {
        service.saveExp(createSimpleExp());
        service.deleteToday();
        List<DailyExp> fromDB = service.findAll();
        assertEquals(0,fromDB.size());
    }

    @Test
    void should_show_exps_for_the_month() {
        service.saveExp(createSimpleExp());

        String toCheck = service.showExpsForTheMonth();
        String target = HelpfulUtils.createDayFormReport(service.findAll().get(0)) + "\n" + "\nExpenses: \n" + "1000,00  /  50000.0 RUB";

        assertEquals(target,toCheck);
    }

    @Test
    void should_show_balance_for_previous_month() {
        int month, year;
        if (MONTH == 0){
            month = 11;
            year = YEAR - 1;
        }
        else {
            month = MONTH-1;
            year = YEAR;
        }
        service.saveExp(new DailyExp(COSTS,DAY,month,year));
        String toCheck = service.showPreviousMonthBalance();
        double sum = service.findByMonthAndYear(MONTH-1, YEAR).stream()
                .mapToDouble(DailyExp::getCost).sum();

        String target = String.valueOf(PLAN - sum);
        assertEquals(target,toCheck);
    }

    private DailyExp createSimpleExp(){
        return new DailyExp(COSTS,DAY,MONTH,YEAR);
    }

}
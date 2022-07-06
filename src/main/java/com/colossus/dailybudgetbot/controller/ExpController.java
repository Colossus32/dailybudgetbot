package com.colossus.dailybudgetbot.controller;

import com.colossus.dailybudgetbot.entity.DailyExp;
import com.colossus.dailybudgetbot.service.ExpService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExpController {

    private final ExpService service;

    public ExpController(ExpService service) {
        this.service = service;
    }

    @PostMapping("/add/{exp}")
    public DailyExp addExp(@PathVariable String exp){
        return service.addExp(exp);
    }

    @GetMapping("/report")
    public String getDailyBudgetPlan(){
        return service.planForRestOfMonth();
    }

    @DeleteMapping("/delete/today")
    public void deleteExpForToday(){
        service.deleteToday();
    }

    @GetMapping("/exps")
    public String getExpsForTheMonth(){
        return service.showExpsForTheMonth();
    }

    @GetMapping("/balance")
    public String showBalance(){
        return service.showPreviousMonthBalance();
    }
}

package com.colossus.dailybudgetbot;

import com.colossus.dailybudgetbot.entity.BudgetBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DailybudgetbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailybudgetbotApplication.class, args);
        new BudgetBot().listen();
    }

}

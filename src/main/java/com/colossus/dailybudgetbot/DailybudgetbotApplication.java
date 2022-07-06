package com.colossus.dailybudgetbot;

import com.colossus.dailybudgetbot.bot.BudgetBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DailybudgetbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DailybudgetbotApplication.class, args);
        new BudgetBot().listen();
    }

}

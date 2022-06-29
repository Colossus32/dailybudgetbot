package com.colossus.dailybudgetbot.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class DailyExp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private double cost;

    @Column(nullable = false)
    private int day;

    @Column(nullable = false)
    private int month;

    @Column(nullable = false)
    private int year;

    public DailyExp(double cost, int day, int month, int year) {
        this.cost = cost;
        this.day = day;
        this.month = month;
        this.year = year;
    }
}

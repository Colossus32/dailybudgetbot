package com.colossus.dailybudgetbot.util;

import com.colossus.dailybudgetbot.entity.DailyExp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HelpfulUtils {
    public static List<Integer> getTodayDate(){ //try to use modern date api , fail like assert 30.06.2022 - get 30.05.2022
        Calendar calendar = Calendar.getInstance();
        int[] monthsNormal = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
        int[] monthsExtra = new int[]{31,29,31,30,31,30,31,31,30,31,30,31};

        List<Integer> list = new ArrayList<>();
        list.add(calendar.getTime().getDate());
        list.add(calendar.getTime().getMonth());
        list.add(calendar.getTime().getYear()+1900);

        int restOfMonth = 0;

        if ((calendar.getTime().getYear() + 1900) % 4 ==0){
            int maxDays = monthsExtra[calendar.getTime().getMonth()];
            restOfMonth = maxDays - list.get(0) + 1; //include current day
        } else {
            int maxDays = monthsNormal[calendar.getTime().getMonth()];
            restOfMonth = maxDays - list.get(0) + 1;//include current day
        }

        list.add(restOfMonth);

        return list;
    }
    public static String createDayFormReport(DailyExp dailyExp){

        String day = String.valueOf(dailyExp.getDay());
        String month = String.valueOf(dailyExp.getMonth());
        String cost = String.valueOf(dailyExp.getCost());

        StringBuilder builder = new StringBuilder();

        if (day.length() < 2) builder.append("0");
        builder.append(day).append(".");

        if (month.length() < 2) builder.append("0");
        builder.append(month).append("  |  ").append(cost);

        return builder.toString();
    }
}

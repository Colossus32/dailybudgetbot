package com.colossus.dailybudgetbot.service.impl;

import com.colossus.dailybudgetbot.entity.DailyExp;
import com.colossus.dailybudgetbot.repository.ExpRepository;
import com.colossus.dailybudgetbot.service.ExpService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpServiceImpl implements ExpService {

    private final ExpRepository repository;

    public ExpServiceImpl(ExpRepository repository) {
        this.repository = repository;
    }

    @Override
    public DailyExp addExp(String numStr) {

        double num = numStr.charAt(0) == '-' ? Double.parseDouble(numStr.substring(1)) * -1 : Double.parseDouble(numStr);

        DailyExp forAdding = findForToday();

        forAdding.setCost(forAdding.getCost()+num);

        repository.save(forAdding);

        return forAdding;
    }

    @Override // just simple calculating for 45000, improve in the future
    public String planForRestOfMonth() {

        int[] calendar = getTodayDate();
        int cMonth = calendar[1];
        int cYear = calendar[2];
        int restOfDays = calendar[3];

        List<DailyExp> list = repository.findAll().stream()
                .filter(dailyExp -> dailyExp.getMonth() == cMonth && dailyExp.getYear() == cYear)
                .collect(Collectors.toList());

        double sumOfExpForThisMonth = list.stream()
                .mapToDouble(DailyExp::getCost)
                .sum();

        return String.valueOf((45000.0 - sumOfExpForThisMonth) / restOfDays);
    }



    private DailyExp findForToday(){

        int[] calendar = getTodayDate();
        int cDay = calendar[0];
        int cMonth = calendar[1];
        int cYear = calendar[2];

        DailyExp toSave = new DailyExp(0,cDay,cMonth,cYear);
        List<DailyExp> list = repository.findAll();
        for (DailyExp e: list) {
            if (e.getDay() == cDay && e.getMonth() == cMonth && e.getYear() == cYear){
                toSave.setCost(e.getCost());
                toSave.setId(e.getId());
            }
        }
        return toSave;
    }

    private int[] getTodayDate(){
        Calendar calendar = Calendar.getInstance();
        int[] monthsNormal = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
        int[] monthsExtra = new int[]{31,29,31,30,31,30,31,31,30,31,30,31};

        int[] result = new int[4];
        result[0] = calendar.getTime().getDay();
        result[1] = calendar.getTime().getMonth();
        result[2] = calendar.getTime().getYear()+1900;

        int restOfMonth = 0;

        if ((calendar.getTime().getYear() + 1900) % 4 ==0){
            int maxDays = monthsExtra[calendar.getTime().getMonth()];
            restOfMonth = maxDays - result[0];
        } else {
            int maxDays = monthsNormal[calendar.getTime().getMonth()];
            restOfMonth = maxDays - result[0];
        }

        result[3] = restOfMonth;
        return result;
    }
}

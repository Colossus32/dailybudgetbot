package com.colossus.dailybudgetbot.service.impl;

import com.colossus.dailybudgetbot.entity.DailyExp;
import com.colossus.dailybudgetbot.repository.ExpRepository;
import com.colossus.dailybudgetbot.service.ExpService;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

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

    @Override // just simple calculating for 50000, improve in the future
    public String planForRestOfMonth() {

        int[] calendar = getTodayDate();
        int cMonth = calendar[1];
        int cYear = calendar[2];
        int restOfDays = calendar[3];

        List<DailyExp> list = repository.findByMonthAndYear(cMonth,cYear);

        double sumOfExpForThisMonth = list.stream()
                .mapToDouble(DailyExp::getCost)
                .sum();
        if (restOfDays == 0) return String.valueOf((50000.0 - sumOfExpForThisMonth));
        return String.valueOf((50000.0 - sumOfExpForThisMonth) / restOfDays);
    }

    @Override
    public void deleteToday() {
        int[] calendar = getTodayDate();
        int cDay = calendar[0];
        int cMonth = calendar[1];
        int cYear = calendar[2];


        repository.findByDate(cDay,cMonth,cYear).stream()
                .findFirst()
                .ifPresent(dailyExp -> repository.deleteById(dailyExp.getId()));
    }

    @Override
    public String showExpsForTheMonth() { // write for bot request !!!

        return null;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////// UTILS

    private DailyExp findForToday(){

        int[] calendar = getTodayDate();
        int cDay = calendar[0]; // 1-31
        int cMonth = calendar[1]; // 0-11
        int cYear = calendar[2]; // 1900 +

        DailyExp toSave = new DailyExp(0,cDay,cMonth,cYear);

        repository.findByDate(cDay,cMonth,cYear).forEach(e -> {
            toSave.setCost(e.getCost());
            toSave.setId(e.getId());
        });

        return toSave;
    }

    private int[] getTodayDate(){ //try to use modern date api , fail like assert 30.06.2022 - get 30.05.2022
        Calendar calendar = Calendar.getInstance();
        int[] monthsNormal = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
        int[] monthsExtra = new int[]{31,29,31,30,31,30,31,31,30,31,30,31};

        int[] result = new int[4];
        result[0] = calendar.getTime().getDate();
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

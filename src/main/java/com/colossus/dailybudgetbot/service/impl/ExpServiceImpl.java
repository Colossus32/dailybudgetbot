package com.colossus.dailybudgetbot.service.impl;

import com.colossus.dailybudgetbot.entity.DailyExp;
import com.colossus.dailybudgetbot.repository.ExpRepository;
import com.colossus.dailybudgetbot.service.ExpService;
import com.colossus.dailybudgetbot.util.HelpfulUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpServiceImpl implements ExpService {

    @Value("${planExp}")
    private double planExp;
    private final ExpRepository repository;

    public ExpServiceImpl(ExpRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public DailyExp addExp(String numStr) {

        double num = numStr.charAt(0) == '-' ? Double.parseDouble(numStr.substring(1)) * -1 : Double.parseDouble(numStr);

        DailyExp forAdding = findForToday();

        forAdding.setCost(forAdding.getCost()+num);

        return saveExp(forAdding);
    }

    @Override // just simple calculating for 50000, improve in the future
    @Transactional
    public String planForRestOfMonth() {

        List<Integer> calendar = HelpfulUtils.getTodayDate();
        int cMonth = calendar.get(1);
        int cYear = calendar.get(2);
        int restOfDays = calendar.get(3);

        List<DailyExp> list = findByMonthAndYear(cMonth,cYear);

        double sumOfExpForThisMonth = list.stream()
                .mapToDouble(DailyExp::getCost)
                .sum();
        if (restOfDays == 0) restOfDays = 1;
        return String.format("%.2f", (planExp - sumOfExpForThisMonth) / restOfDays) + " RUB";
    }

    @Override
    @Transactional
    public void deleteToday() {

        List<Integer> calendar = HelpfulUtils.getTodayDate();
        int cDay = calendar.get(0);
        int cMonth = calendar.get(1);
        int cYear = calendar.get(2);

        findByDate(cDay,cMonth,cYear).stream()
                .findFirst()
                .ifPresent(dailyExp -> deleteById(dailyExp.getId()));
    }

    @Override
    @Transactional
    public String showExpsForTheMonth() {
        List<Integer> calendar = HelpfulUtils.getTodayDate();
        StringBuilder builder = new StringBuilder();
        findByMonthAndYear(calendar.get(1),calendar.get(2))
                .forEach(dailyExp -> {
                    builder.append(HelpfulUtils.createDayFormReport(dailyExp)).append("\n");
                });
        return builder.toString();
    }

    @Override
    public String showPreviousMonthBalance() {
        List<Integer> calendar = HelpfulUtils.getTodayDate();
        int month = calendar.get(1) - 1;
        int year = calendar.get(2);
        if (month < 0){
            month = 11;
            year--;
        }
        double sum = findByMonthAndYear(month,year).stream()
                .mapToDouble(DailyExp::getCost).sum();

        return String.valueOf(planExp - sum);
    }

///////////////////////////////////////////////////////////////////////////////////////////////////// UTILS
    @Override
    public DailyExp saveExp(DailyExp dailyExp){
        return repository.save(dailyExp);
    }

    @Override
    public List<DailyExp> findByMonthAndYear(int month, int year){
        return repository.findByMonthAndYear(month,year);
    }

    @Override
    public List<DailyExp> findByDate(int day, int month, int year){
        return repository.findByDate(day,month,year);
    }

    @Override
    public void deleteById (long id){
        repository.deleteById(id);
    }

    @Override
    public List<DailyExp> findAll() {
        return repository.findAll();
    }

    private DailyExp findForToday(){

        List<Integer> calendar = HelpfulUtils.getTodayDate();
        int cDay = calendar.get(0); // 1-31
        int cMonth = calendar.get(1); // 0-11
        int cYear = calendar.get(2); // 1900 +

        DailyExp toSave = new DailyExp(0,cDay,cMonth,cYear);

        findByDate(cDay,cMonth,cYear).forEach(e -> {
            toSave.setCost(e.getCost());
            toSave.setId(e.getId());
        });

        return toSave;
    }


}

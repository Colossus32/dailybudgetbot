package com.colossus.dailybudgetbot.repository;

import com.colossus.dailybudgetbot.entity.DailyExp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class CustomRepositoryImpl implements CustomRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<DailyExp> findByMonthAndYear(int month, int year) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<DailyExp> dailyExp = cq.from(DailyExp.class);

        Predicate monthPredicate = cb.equal(dailyExp.get("month"),month);
        Predicate yearPredicate = cb.equal(dailyExp.get("year"), year);

        cq.where(monthPredicate,yearPredicate);
        cq.select(dailyExp);

        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    @Transactional
    public List<DailyExp> findByDate(int day, int month, int year) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();

        Root<DailyExp> dailyExp = cq.from(DailyExp.class);

        Predicate dayPredicate = cb.equal(dailyExp.get("day"), day);
        Predicate monthPredicate = cb.equal(dailyExp.get("month"),month);
        Predicate yearPredicate = cb.equal(dailyExp.get("year"), year);

        cq.where(dayPredicate, monthPredicate, yearPredicate);
        cq.select(dailyExp);

        return entityManager.createQuery(cq).getResultList();
    }
}

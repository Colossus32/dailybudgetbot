package com.colossus.dailybudgetbot.repository;

import com.colossus.dailybudgetbot.entity.ChatId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatIdRepository extends JpaRepository<ChatId, Long> {
    List<ChatId> findBySubscribe(boolean sub);
}

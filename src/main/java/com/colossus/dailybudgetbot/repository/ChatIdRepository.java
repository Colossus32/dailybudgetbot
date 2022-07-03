package com.colossus.dailybudgetbot.repository;

import com.colossus.dailybudgetbot.entity.ChatId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatIdRepository extends JpaRepository<ChatId, Long> {
}

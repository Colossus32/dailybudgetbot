package com.colossus.dailybudgetbot.service;

import com.colossus.dailybudgetbot.entity.ChatId;

public interface ChatIdService {
    void saveChatId(Long chatId);
    ChatId getChatId(long id);
    String getAllSubscribers();
    boolean changeSubscribe(long id);
}

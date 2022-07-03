package com.colossus.dailybudgetbot.service.impl;

import com.colossus.dailybudgetbot.entity.ChatId;
import com.colossus.dailybudgetbot.repository.ChatIdRepository;
import com.colossus.dailybudgetbot.service.ChatIdService;
import org.springframework.stereotype.Service;

@Service
public class ChatIdServiceImpl implements ChatIdService {

    private final ChatIdRepository repository;

    public ChatIdServiceImpl(ChatIdRepository repository) {
        this.repository = repository;
    }


    @Override
    public void saveChatId(Long chatId) {
        repository.save(new ChatId(chatId));
    }

    @Override
    public ChatId getChatId(long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public String getAllSubscribers() {
        StringBuilder builder = new StringBuilder();
        repository.findAll().stream().filter(ChatId::isSubscribe).map(ChatId::getId).forEach(id -> builder.append(id).append(" "));
        return builder.toString().trim();
    }

    @Override
    public boolean changeSubscribe(long id) {
        ChatId chatId = repository.findById(id).orElse(new ChatId(id));
        chatId.setSubscribe(!chatId.isSubscribe());
        repository.save(chatId);
        return chatId.isSubscribe();
    }
}

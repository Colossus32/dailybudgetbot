package com.colossus.dailybudgetbot.service.impl;

import com.colossus.dailybudgetbot.entity.ChatId;
import com.colossus.dailybudgetbot.repository.ChatIdRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class ChatIdServiceImplTest {
    private final Long CHATID = 1L;

    @Autowired
    ChatIdServiceImpl service;
    @Autowired
    ChatIdRepository repository;

    @AfterEach
    void tearDown() {
        log.info("Cleaning chatIds");
        List<ChatId> list = repository.findAll();
        if (list.size()>0) {
            log.info("There are some ChatIds in the database: {}", list.size());
            repository.deleteAll();
        }
    }

    @Test
    void should_save_chatId() {
        ChatId toSave = new ChatId(CHATID);
        service.saveChatId(toSave.getId());
        ChatId fromDB = service.getChatId(CHATID);

        assertEquals(toSave.getId(),fromDB.getId());
        assertEquals(toSave.isSubscribe(),fromDB.isSubscribe());
    }

    @Test
    void should_get_chatId() {
        service.saveChatId(CHATID);
        ChatId fromDB = service.getChatId(CHATID);

        assertEquals(CHATID, fromDB.getId());
        assertTrue(fromDB.isSubscribe());
    }

    @Test
    void should_not_get_chatId_and_return_null() {
        service.saveChatId(CHATID);
        ChatId requiredChatId = service.getChatId(2L);

        assertNull(requiredChatId);
    }

    @Test
    void should_get_all_subscribed_chatIds() {
        createListOfChatIds();
        String[] subscribers = service.getAllSubscribers().split(" ");

        assertEquals(4, subscribers.length);
    }

    @Test
    void should_change_subscribe_for_chatId() {
        service.saveChatId(CHATID);

        service.changeSubscribe(CHATID);

        ChatId fromDB = service.getChatId(CHATID);

        assertFalse(fromDB.isSubscribe());
    }

    private void createListOfChatIds(){
        service.saveChatId(CHATID);
        service.saveChatId(CHATID + 1);
        service.saveChatId(CHATID + 2);
        service.saveChatId(CHATID + 3);

    }
}
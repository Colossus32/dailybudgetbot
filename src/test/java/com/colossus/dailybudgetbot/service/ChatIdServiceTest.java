package com.colossus.dailybudgetbot.service;

import com.colossus.dailybudgetbot.entity.ChatId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatIdServiceTest {

    @Autowired
    private ChatIdService service;

    @Test
    void saveChatId() {
        service.saveChatId(1l);
        ChatId fromDB = service.getChatId(1l);
        assertEquals(1L, fromDB.getId());
        assertTrue(fromDB.isSubscribe());
    }

    @Test
    void getChatId() {
        service.saveChatId(2L);
        ChatId fromDB = service.getChatId(2L);
        assertEquals(2L, fromDB.getId());
        assertTrue(fromDB.isSubscribe());
    }

    @Test
    void getAllSubscribers() {
        service.saveChatId(3L);
        service.saveChatId(4L);
        String result = service.getAllSubscribers();
        assertEquals("3 4", result);
    }

    @Test
    void changeSubscribe() {
        assertFalse(service.changeSubscribe(3L));
    }
}
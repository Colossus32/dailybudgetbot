package com.colossus.dailybudgetbot.controller;

import com.colossus.dailybudgetbot.entity.ChatId;
import com.colossus.dailybudgetbot.service.ChatIdService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final ChatIdService service;

    public ChatController(ChatIdService service) {
        this.service = service;
    }

    @PostMapping
    @Transactional
    public void saveId(@RequestParam("id") Long id){
        service.saveChatId(id);
    }

    @GetMapping
    @Transactional
    public ChatId getChatId (long id){
        return service.getChatId(id);
    }

    @GetMapping("/all")
    @Transactional
    public String getSubscribers(){
        return service.getAllSubscribers();
    }

    @PatchMapping("/subscriber")
    @Transactional
    public boolean changeSubscribe(@RequestParam("id") Long id){
        return service.changeSubscribe(id);
    }
}

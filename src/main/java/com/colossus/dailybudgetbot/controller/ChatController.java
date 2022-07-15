package com.colossus.dailybudgetbot.controller;

import com.colossus.dailybudgetbot.entity.ChatId;
import com.colossus.dailybudgetbot.service.ChatIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chats")
@Transactional
@RequiredArgsConstructor
public class ChatController {

    private final ChatIdService service;

    @PostMapping
    public void saveId(@RequestParam("id") Long id){
        service.saveChatId(id);
    }

    @GetMapping
    public ChatId getChatId (long id){
        return service.getChatId(id);
    }

    @GetMapping("/all")
    public String getSubscribers(){
        return service.getAllSubscribers();
    }

    @PatchMapping("/subscriber")
    public boolean changeSubscribe(@RequestParam("id") Long id){
        return service.changeSubscribe(id);
    }
}

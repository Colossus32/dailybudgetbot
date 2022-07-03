package com.colossus.dailybudgetbot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChatId {

    @Id
    private long id;
    private boolean subscribe;

    public ChatId(long id) {
        this.id = id;
        this.subscribe = true;
    }
}

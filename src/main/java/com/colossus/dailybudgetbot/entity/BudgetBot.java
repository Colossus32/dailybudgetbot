package com.colossus.dailybudgetbot.entity;

import com.colossus.dailybudgetbot.service.ExpService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BudgetBot {

    TelegramBot bot;

    public BudgetBot() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("token.txt"));
            String token = reader.readLine();
            reader.close();
            this.bot = new TelegramBot(token);
        } catch (Exception e){
            System.out.println("bot initialisation error");
            e.printStackTrace();
        }
    }

    public void listen(){

        bot.setUpdatesListener(element ->{
            element.forEach(pock ->{

                HttpClient client = HttpClient.newHttpClient();

                if (pock.message().text().equals("plan")){
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8081/api/report"))
                            .build();
                    try {
                        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
                        bot.execute(new SendMessage(pock.message().chat().id(), response.body()));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                String[] arr = pock.message().text().trim().split(" ");

                for (String s: arr) {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8081/api/add/" + s))
                            .build();

                }

            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}

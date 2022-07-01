package com.colossus.dailybudgetbot.entity;

import com.google.gson.internal.$Gson$Preconditions;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

                //refactor it, make it switch
                if (pock.message().text() != null) {
                    //greetings
                    if (pock.message().text().equals("/start")) {
                        bot.execute(new SendMessage(pock.message().chat().id(), "welcome, let's start"));
                    }
                    //delete today if made a mistake
                    if (pock.message().text().equals("/delete")){
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8081/api/delete/today"))
                                .DELETE()
                                .build();
                        try {
                            client.send(request, HttpResponse.BodyHandlers.ofString());
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        bot.execute(new SendMessage(pock.message().chat().id(), "deleting today's data..."));
                    }
                    //get daily plan for the rest of the month
                    if (pock.message().text().equalsIgnoreCase("plan")) {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8081/api/report"))
                                .build();
                        try {
                            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                            bot.execute(new SendMessage(pock.message().chat().id(), response.body()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //parsing text from the message
                        String[] arr = pock.message().text().trim().split(" ");

                        for (String s : arr) {

                            //a checker for invalid inputs
                            if (checkString(s)) {

                                HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create("http://localhost:8081/api/add/" + s))
                                        .POST(HttpRequest.BodyPublishers.noBody())
                                        .build();
                                try {
                                    client.send(request, HttpResponse.BodyHandlers.ofString());
                                } catch (IOException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                                bot.execute(new SendMessage(pock.message().chat().id(), s + " is excepted"));
                            } else {
                                bot.execute(new SendMessage(pock.message().chat().id(), "Error: "+ s + " is invalid input. Try something like this: -800 100 50.5"));
                            }
                        }
                    }
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private boolean checkString(String s){

        if (s.charAt(0) == '-') s = s.substring(1);

        for (int i = 0; i < s.length(); i++) {

            if (!Character.isDigit(s.charAt(i))) return false;
        }

        return true;
    }
}

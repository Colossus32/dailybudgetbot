package com.colossus.dailybudgetbot.entity;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class BudgetBot {

    TelegramBot bot;

    public BudgetBot() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("token.txt"));
            String token = reader.readLine();
            reader.close();
            this.bot = new TelegramBot(token);
        } catch (Exception e) {
            System.out.println("bot initialisation error");
            e.printStackTrace();
        }
    }

    public void listen() {

        bot.setUpdatesListener(element -> {
            element.forEach(pock -> {

                HttpClient client = HttpClient.newHttpClient();

                if (pock.message().text() != null) {
                    String text = pock.message().text().toLowerCase();
                    Long chatId = pock.message().chat().id();
                    switch (text) {
                        case "/start":
                            //save chatID to the database
                            HttpRequest requestForSave = HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8081/api/chats?id=" + chatId))
                                    .POST(HttpRequest.BodyPublishers.noBody())
                                    .build();
                            try {
                                client.send(requestForSave, HttpResponse.BodyHandlers.ofString());
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }

                            //greetings
                            bot.execute(new SendMessage(chatId, "welcome, let's start"));
                            botMenu(chatId);
                            break;

                        case "/help":
                            botMenu(chatId);
                            break;

                        case "/month":
                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8081/api/exps"))
                                    .build();
                            try {
                                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                                bot.execute(new SendMessage(chatId, response.body()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;

                        case "/delete":
                            request = HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8081/api/delete/today"))
                                    .DELETE()
                                    .build();
                            try {
                                client.send(request, HttpResponse.BodyHandlers.ofString());
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            bot.execute(new SendMessage(chatId, "deleting today's data..."));
                            break;

                        case "/plan":
                            request = HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8081/api/report"))
                                    .build();
                            try {
                                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                                bot.execute(new SendMessage(chatId, response.body()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;

                        case "/subscribe":

                            request = HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8081/api/chats/subscribe?id=" + chatId))
                                    .PUT(HttpRequest.BodyPublishers.noBody())
                                    .build();

                            try {
                                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                                String subscribeText;
                                if (response.body().equals("false")) subscribeText = "Now you're not subscribed.";
                                else subscribeText = "Now you're subscribed for the reports.";
                                bot.execute(new SendMessage(chatId, subscribeText));

                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }

                            break;

                        default:
                            String[] arr = pock.message().text().trim().split(" ");

                            for (String s : arr) {

                                //a checker for invalid inputs
                                if (checkString(s)) {

                                    request = HttpRequest.newBuilder()
                                            .uri(URI.create("http://localhost:8081/api/add/" + s))
                                            .POST(HttpRequest.BodyPublishers.noBody())
                                            .build();
                                    try {
                                        client.send(request, HttpResponse.BodyHandlers.ofString());
                                    } catch (IOException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    bot.execute(new SendMessage(chatId, s + " is excepted"));
                                } else {
                                    bot.execute(new SendMessage(chatId, "Error: " + s + " is invalid input. Try something like this: -800 100 50.5"));
                                }
                            }
                            break;
                    }
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }


    private boolean checkString(String s) {

        if (s.charAt(0) == '-') s = s.substring(1);

        for (int i = 0; i < s.length(); i++) {

            if (!Character.isDigit(s.charAt(i))) return false;
        }

        return true;
    }

    //@Scheduled(fixedDelayString = )
    //@Scheduled(fixedDelay = 3000L)
    //@Scheduled(cron = "${report.testdelay}")
    //@Scheduled(cron = "*/10 * * * * *")
    @Scheduled(cron = "${report.delay}")
    public void scheduledReport(){

        //get all chatId subscribed
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/api/chats/all"))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String[] list = response.body().split(" ");

            //get balance for previous month
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/api/balance"))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            String balance = response.body();

            //send message to all subscribers
            for (String id: list) {
                bot.execute(new SendMessage(Long.parseLong(id), "Previous month balance: " + balance + " RUB"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "${report.dailyreminder}")
    //@Scheduled(cron = "${report.dailyremindertest}")
    public void sendDailyReminder(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/api/chats/all"))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String[] list = response.body().split(" ");

            //get daily plan
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/api/report"))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            String plan = response.body();

            //send message to all subscribers
            for (String id: list) {
                bot.execute(new SendMessage(Long.parseLong(id), "Daily plan: " + plan + " RUB"));
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public void botMenu(Long chatId){
        bot.execute(new SendMessage(chatId, "100 -50 25.5    add expensive for the current day\n" +
                "/plan         get the daily plan for the rest of the month\n" +
                "/delete       delete the current day's data\n" +
                "/month        get exps for each day of the month\n" +
                "/subscribe    change your subscribe for reports"));
    }
}

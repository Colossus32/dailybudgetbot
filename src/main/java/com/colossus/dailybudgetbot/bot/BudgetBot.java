package com.colossus.dailybudgetbot.bot;

import com.colossus.dailybudgetbot.util.HelpfulUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class BudgetBot {

    TelegramBot bot;

    public BudgetBot() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("G:\\_JAVA\\dailybudgetbot\\token.txt"));
            String token = reader.readLine();
            reader.close();
            this.bot = new TelegramBot(token);
        } catch (Exception e) {
            log.error("bot initialisation error");
            e.printStackTrace();
        }
    }

    public void listen() {

        bot.setUpdatesListener(element -> {
            element.forEach(pock -> {

                if (pock.message() != null && pock.message().text() != null) {
                    String text = pock.message().text().toLowerCase();
                    Long chatId = pock.message().chat().id();
                    log.info("Got a new not null message from {}", chatId);

                    switch (text) {

                        case "/start":
                            botStart(chatId);
                            break;

                        case "/help":
                            botMenu(chatId);
                            break;

                        case "/month":
                            botMonth(chatId);
                            break;

                        case "/delete":
                            botDelete(chatId);
                            break;

                        case "/plan":
                            botPlan(chatId);
                            break;

                        case "/subscribe":
                            botSubscribe(chatId);
                            break;

                        default:
                            botDefault(chatId,pock.message().text().trim().split(" "));
                            break;
                    }
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////Schedule block
    //@Scheduled(cron = "${report.testdelay}")
    @Scheduled(cron = "${report.delay}")
    public void scheduledPreviousMonthReport(){

        //get all chatId subscribed
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/api/v1/chats/all"))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String[] list = response.body().split(" ");

            //get balance for previous month
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/api/v1/balance"))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            String balance = response.body();

            //send message to all subscribers
            for (String id: list) {
                bot.execute(new SendMessage(Long.parseLong(id), "Previous month balance: " + balance));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "${report.dailyreminder}")
    //@Scheduled(cron = "${report.dailyremindertest}")
    public void sendDailyReminder(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/api/v1/chats/all"))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String[] list = response.body().split(" ");

            //get daily plan
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/api/v1/report"))
                    .build();
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            String plan = response.body();

            //send message to all subscribers
            for (String id: list) {
                bot.execute(new SendMessage(Long.parseLong(id), "Daily plan: " + plan));
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "${report.rewriteplan}")
    //@Scheduled(cron = "${report.rewriteplantest}")
    public void recalculateDailyPlan(){
        HelpfulUtils.recalculatePlan();
    }

    @Scheduled(cron = "${report.backupperiod}")
    //@Scheduled(cron = "${report.backupperiodtest}")
    public void cleanBackUp(){

        try {
            HelpfulUtils.cleanBackUp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////Schedule block
    private void botMenu(Long chatId){
        bot.execute(new SendMessage(chatId, "100 -50 25.5    add expensive for the current day\n" +
                "/plan         get the daily plan for the rest of the month\n" +
                "/delete       delete the current day's data\n" +
                "/month        get exps for each day of the month\n" +
                "/subscribe    change your subscribe for reports"));
    }

    private void botStart(Long chatId){
        //save chatID to the database
        HttpRequest requestForSave = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/api/v1/chats?id=" + chatId))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpClient.newHttpClient().send(requestForSave, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        //greetings
        bot.execute(new SendMessage(chatId, "welcome, let's start"));
        botMenu(chatId);
    }

    private void botMonth(Long chatId){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/api/v1/exps"))
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            bot.execute(new SendMessage(chatId, response.body()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void botDelete(Long chatId){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/api/v1/today"))
                .DELETE()
                .build();
        try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        bot.execute(new SendMessage(chatId, "deleting today's data..."));
    }

    private void botPlan(Long chatId){

        try {
            String answer = HelpfulUtils.readDailyPlanFromTheFile() + " RUB";
            bot.execute(new SendMessage(chatId, answer));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void botSubscribe(Long chatId){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/api/v1/chats/subscriber?id=" + chatId))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String subscribeText = "Now you're subscribed for the reports.";
            if (response.body().equals("false")) subscribeText = "Now you're not subscribed.";
            bot.execute(new SendMessage(chatId, subscribeText));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void botDefault(Long chatId, String[] arr){

        for (String s : arr) {

            //a checker for invalid inputs
            if (HelpfulUtils.checkString(s)) {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8081/api/v1/add/" + s))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                try {
                    HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    HelpfulUtils.writeMessageToFile(chatId,s);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                bot.execute(new SendMessage(chatId, s + " is excepted"));
            } else {
                bot.execute(new SendMessage(chatId, "Error: " + s + " is invalid input. Try something like this: -800 100 50.5\n" +
                        "or use /help to see available commands"));
            }
        }
    }
}

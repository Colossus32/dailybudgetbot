package com.colossus.dailybudgetbot.util;

import com.colossus.dailybudgetbot.entity.DailyExp;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HelpfulUtils {
    public static List<Integer> getTodayDate(){ //try to use modern date api , fail like assert 30.06.2022 - get 30.05.2022
        Calendar calendar = Calendar.getInstance();
        int[] monthsNormal = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
        int[] monthsExtra = new int[]{31,29,31,30,31,30,31,31,30,31,30,31};

        List<Integer> list = new ArrayList<>();
        list.add(calendar.getTime().getDate());
        list.add(calendar.getTime().getMonth());
        list.add(calendar.getTime().getYear()+1900);

        int restOfMonth = 0;

        if ((calendar.getTime().getYear() + 1900) % 4 ==0){
            int maxDays = monthsExtra[calendar.getTime().getMonth()];
            restOfMonth = maxDays - list.get(0) + 1; //include current day
        } else {
            int maxDays = monthsNormal[calendar.getTime().getMonth()];
            restOfMonth = maxDays - list.get(0) + 1;//include current day
        }

        list.add(restOfMonth);

        return list;
    }
    public static String createDayFormReport(DailyExp dailyExp){

        String day = String.valueOf(dailyExp.getDay());
        String month = String.valueOf(dailyExp.getMonth());
        String cost = String.valueOf(dailyExp.getCost());

        StringBuilder builder = new StringBuilder();

        if (day.length() < 2) builder.append("0");
        builder.append(day).append(".");

        if (month.length() < 2) builder.append("0");
        builder.append(month).append("  |  ").append(cost);

        return builder.toString();
    }

    public static boolean checkString(String s) {

        if (s.charAt(0) == '-') s = s.substring(1);

        if (s.length() > 6) return false;
        for (int i = 0; i < s.length(); i++) {

            if (!Character.isDigit(s.charAt(i))) return false;
        }

        return true;
    }

    public static void saveToFileTodayDailyBudget(String s) throws IOException {

        try(FileWriter fw = new FileWriter("G:\\_JAVA\\dailybudgetbot\\dailyplan.txt");
            BufferedWriter writer = new BufferedWriter(fw)){
            writer.write(s);
            writer.flush();
        }
    }

    public static String readDailyPlanFromTheFile() throws IOException {

        File file = new File("G:\\_JAVA\\dailybudgetbot\\dailyplan.txt");
        if (!file.exists()) recalculatePlan();

        try(FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr)){
            return reader.readLine();
        }
    }

    public static void writeMessageToFile(Long chatId, String toSave) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter("G:\\_JAVA\\dailybudgetbot\\dailybackup.txt", true));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String time = dateFormat.format(new Timestamp(System.currentTimeMillis()));

        writer.write(String.format("date: %s    user: %d    message: %s\n",time, chatId,toSave));
        writer.flush();
        writer.close();
    }

    public static void cleanBackUp() throws IOException {
        File f = new File("G:\\_JAVA\\dailybudgetbot\\dailybackup.txt");
        if (f.delete()){
            System.out.println("Backup deleted.");
        } else {
            System.out.println("There is no backup file");
        }
    }

    public static void recalculatePlan(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/api/v1/report"))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String fromFile = response.body();
            String toSave = fromFile.substring(0, fromFile.indexOf(" "));
            HelpfulUtils.saveToFileTodayDailyBudget(toSave);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

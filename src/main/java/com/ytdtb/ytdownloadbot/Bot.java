package com.ytdtb.ytdownloadbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Bot extends TelegramLongPollingBot {

    ExecutorService executorService =
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());

    public String getBotUsername() {
        return "ytdtbot-name";
    }

    @Override
    public String getBotToken() {
        return "7611440251:AAH9Axd6G7EtKu4X3qgUit7i9olU0X9dcTk";
    }


    int mode = -1;
    int MODE_SAVE = 1;
    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();

        if(msg.isCommand()){
            if(msg.getText().equals("/save")) {
                mode = MODE_SAVE;
                String link = "http://localhost:8080/data/WidNsNk8vGE";
                String formatted = "[inline URL]("+link+")";

                sendText(id, formatted);
            }
            else {
                sendText(id, "other command");
            }
        } else {
            if (mode == MODE_SAVE) {
                String link = msg.getText();
                var youtube_id = getYoutubeId(link);
                String new_link ="http://135.181.101.239:8080/data/"+youtube_id;
                String test = "[inline URL]("+new_link+")";
                sendText(id, test);

                try {
                    var resp_msg = downloadCommand(msg.getText());
                    sendText(id, resp_msg);
                } catch (Exception e) {
                    sendText(id, "error");
                    System.out.println(e);
                }
            }
        }
    }



    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

    private String getYoutubeId(String link) {
        //https://youtu.be/WidNsNk8vGE?si=drepREIB3iK8tu-4
        System.out.println(link);
        int lastSlash = link.lastIndexOf("/");
        System.out.println(lastSlash);
        int qIndex = link.lastIndexOf("?");
        if (qIndex == -1) {
            qIndex = link.length();
        }
        System.out.println(qIndex);
        String id = link.substring(lastSlash+1, qIndex);
        return id;
    }

    private String downloadCommand(String link) throws InterruptedException, IOException {
        String cmd = "/home/ilia/yt-dlp/yt-dlp --cookies-from-browser firefox:7iezo0zo.default "+link;
        System.out.println("Command: "+cmd);
        Process process;
            process = Runtime.getRuntime()
                    .exec(String.format(cmd));//"/bin/sh -c ls %s", homeDirectory));
        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        Future<?> future = executorService.submit(streamGobbler);

        int exitCode = process.waitFor();

        var id = getYoutubeId(link);
        String returnUrl ="http://135.181.101.239:8080/data/"+id;
        return id;
    }
    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

}
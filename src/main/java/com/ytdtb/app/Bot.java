package com.ytdtb.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Bot extends TelegramLongPollingBot {

    Logger log = Logger.getLogger(Bot.class.getName());

    @Value( "${bot.username}" )
    public String botUsername;

    @Value( "${bot.token}" )
    public String botToken;

    @Value( "${server.url}" )
    public String serverUrl;

    @Value( "${ytdlp.path}" )
    public String ytdlpPath;

    @Value( "${firefox.session}" )
    public String firefoxSession;

    public String lastYouTubeId;

    @EventListener(ApplicationReadyEvent.class)
    public void startBot() throws TelegramApiException {
        log.log(Level.INFO, "BOT_USERNAME: "+ this.botUsername);
        log.log(Level.INFO, "FIREFOX_SESSION: "+ this.firefoxSession);
        log.log(Level.INFO, "SERVER_URL: "+ this.serverUrl);
        log.log(Level.INFO, "YTDLP_PATH: "+ this.ytdlpPath);

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
        log.log(Level.INFO, "Bot started");

    }


    ExecutorService executorService =
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());

    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    int mode = -1;
    int COMMAND_START = 1;
    int COMMAND_DOWNLOAD = 2;
    int COMMAND_LAST = 3;
    int COMMAND_TEST = 4;
    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();

        log.log(Level.INFO, "User ID: " +id);
        log.log(Level.INFO, "User: "+user);

        if(msg.isCommand()){

            String cmd = msg.getText();
            log.log(Level.INFO, "Command: "+msg.getText());

            if(cmd.equals("/download")) {
                mode = COMMAND_DOWNLOAD;
                String text = "Give me a link to the video";//, for example [link](https://www.youtube.com/watch?v=_CC2Uaxp2DU)";
                sendText(id, text);
            } else if(cmd.equals("/start")) {
                String text = "Welcome to the bot! Use /download command to start the download process";
                sendText(id, text);
            } else if(cmd.equals("/last")) {
                if (lastYouTubeId!=null) {
                    String last_link = serverUrl + "/last";
                    sendText(id, "Link to the last video: ["+last_link+"]");
                }
            } else if(cmd.equals("/test")) {
                mode = COMMAND_TEST;
                String text_html = "<a href='https://www.google.com/'>Google</a>";
                sendText(id, text_html, ParseMode.HTML);
                String text_md = "[google](https://www.google.com/)";
                sendText(id, text_md, ParseMode.MARKDOWN);
            } else {
                sendText(id, "unknown command");
            }
        } else {
            log.log(Level.INFO, "Text: " +msg.getText());

            if (mode == COMMAND_DOWNLOAD) {
                String link = msg.getText();
                var youtube_id = getYoutubeId(link);
                sendText(id, "Please wait...");

                try {
                    var code = downloadCommand(msg.getText());
                    if (code == 0) {
                        String v_link = serverUrl + "/v/"+ youtube_id;
                        String dl_link = serverUrl + "/dl/"+ youtube_id;
//                        sendText(id, "Click [HERE]("+new_link+") to see video", ParseMode.MARKDOWN);
//                        sendText(id, "Link to [video]("+new_link+") is here", ParseMode.MARKDOWNV2);
//                        sendText(id, "<a href=\""+new_link+ "\">CLICK HERE TO SEE VIDEO</a>", ParseMode.HTML);
//                        sendText(id, "<a href=\""+new_link+ "\">CLICK HERE TO SEE VIDEO</a>");
//                        sendText(id, "Link to [video]("+new_link+") is here");
//                        sendText(id, "("+new_link+")");
                        sendText(id, "Enjoy: ["+v_link+"]");
                        sendText(id, "Download: ["+dl_link+"]");

                        lastYouTubeId = youtube_id;
//                        sendText(id, "<"+new_link+">");


                    } else {
                        sendText(id, "Cannot download this link!");
                    }

                } catch (Exception e) {
                    log.log(Level.INFO, "Error", e);
                    sendText(id, "Cannot download this link!");
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
        int lastSlash = link.lastIndexOf("/");
        int qIndex = link.lastIndexOf("?");
        if (qIndex == -1) {
            qIndex = link.length();
        }
        String id = link.substring(lastSlash+1, qIndex);
        return id;
    }

    private int downloadCommand(String link) throws InterruptedException, IOException {
        String cmd = ytdlpPath + " --cookies-from-browser firefox:"+firefoxSession+" "+link;
        log.log(Level.INFO, "Invoking :" + cmd);
        Process process;
            process = Runtime.getRuntime()
                    .exec(String.format(cmd));
        StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        Future<?> future = executorService.submit(streamGobbler);

        int exitCode = process.waitFor();
        log.log(Level.INFO, "Code: "+exitCode);

        return exitCode;

    }

    public void sendText(Long who, String what){
        sendText(who, what, null);
    }

    public void sendText(Long who, String what, String parseMode){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .parseMode(parseMode)
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

}
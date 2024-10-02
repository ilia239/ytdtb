package com.ytdtb.ytdownloadbot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    public String getBotUsername() {
        return "ytdtbot-name";
    }

    @Override
    public String getBotToken() {
        return "7611440251:AAH9Axd6G7EtKu4X3qgUit7i9olU0X9dcTk";
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();

        if(msg.isCommand()){
            if(msg.getText().equals("/save")) {
                sendText(id, "save command");
            }
            else {
                sendText(id, "other command");
            }
        } else {
            sendText(id, msg.getText()+ " received");
        }
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
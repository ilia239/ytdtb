package com.ytdtb.ytdownloadbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class YtdownloadbotApplication {

	public static void main(String[] args) throws TelegramApiException {


		SpringApplication.run(YtdownloadbotApplication.class, args);

		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		botsApi.registerBot(new Bot());
	}

}

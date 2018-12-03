 package com.arg.project;
 import org.telegram.telegrambots.*;
 import org.telegram.telegrambots.bots.TelegramLongPollingBot;
 import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
 import org.telegram.telegrambots.meta.api.objects.Update;
 import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

 public class MyAmazingBot extends TelegramLongPollingBot {
    public static String playercommand;
    //@Override
    public void onUpdateReceived(Update update) {

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            playercommand = message_text;
            System.out.println(playercommand);
            System.out.println("codetesting");
            if(message_text == "end"){

                String message_text6 = "player turn ended";

                SendMessage message = new SendMessage() // Create a message object object
                    .setChatId(chat_id)
                    .setText(message_text6);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            else {
                //return what the player typed
            SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(message_text);
            try {
                execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            }
        }
    }
    
    public static void sendSampleText(String message_text2){

        long chat_id3 = -254512808;
        String message_text3 = message_text2;
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id3)
                .setText(message_text3);
            try {
                new MyAmazingBot().execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
    }

    //@Override
    public String getBotUsername() {
        // Return bot username
        // If bot username is @MyAmazingBot, it must return 'MyAmazingBot'
        return "RiskProjectBot";
    }

    public String getBotToken() {
        return "757130470:AAGMcBp69MXiOv_dtdjGbmCIldC9D7B5g2E";
    }
}

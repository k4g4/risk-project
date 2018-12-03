 package com.arg.project;
 //import org.telegram.telegrambots;
 //import org.telegram.*;
 //import com.arg.project.Board;
 import org.telegram.telegrambots.*;
 //import org.telegram.telegrambots.api.methods.send.SendMessage;
// import org.telegram.telegrambots.api.objects.Update;
 import org.telegram.telegrambots.bots.TelegramLongPollingBot;
 import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
 import org.telegram.telegrambots.meta.api.objects.Update;
// import org.telegram.telegrambots.exceptions.TelegramApiException;
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
                //Board.nextPlayer();
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
            //String message_text = Long.toString(chat_id);
            //String message_text = update.getMessage().getChatId();
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
        //String message_text = update.getMessage().getChatId();
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id3)
                .setText(message_text3);
            try {
                new MyAmazingBot().execute(message); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

    }
    //public
       /* public void commandReader(Update update){
        if (update.hasMessage() && update.getMessage().hasText()) {
            // Set variables
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            playercommand = message_text;
            //String newstring;
            //newstring = message_text;

            SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(message_text);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                //return newstring;
        }
        //String newstring= update.getMessage().getText();
            //newstring = message_text;
        //return newstring;
    }*/

    //@Override
    public String getBotUsername() {
        // Return bot username
        // If bot username is @MyAmazingBot, it must return 'MyAmazingBot'
        return "RiskProjectBot";
    }

    //@Override
    public String getBotToken() {
        // Return bot token from BotFather
        return "757130470:AAGMcBp69MXiOv_dtdjGbmCIldC9D7B5g2E";
    }
}

package com.padelscore.telegram;

import com.padelscore.telegram.handler.CommandHandler;
import com.padelscore.telegram.handler.CallbackHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class PadelScoreBot extends TelegramLongPollingBot {
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    @Value("${telegram.bot.username}")
    private String botUsername;
    
    private final CommandHandler commandHandler;
    private final CallbackHandler callbackHandler;
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    
    @Override
    public String getBotToken() {
        return botToken;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            commandHandler.handle(update.getMessage(), this);
        } else if (update.hasCallbackQuery()) {
            callbackHandler.handle(update.getCallbackQuery(), this);
        }
    }
}

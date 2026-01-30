package com.padelscore.telegram.handler.callback;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface Callback {

    void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot);

    boolean coincidence(String command);
}

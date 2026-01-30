package com.padelscore.telegram.handler.command;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface Command {

    void handle(Message message, TelegramLongPollingBot bot);

    boolean coincidence(String command);
}

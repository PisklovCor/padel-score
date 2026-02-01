package com.padelscore.telegram.handler.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackHelp implements Callback {

  @Override
  public boolean coincidence(String command) {

    return "help".equals(command);
  }

  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {

    final var chatId = callbackQuery.getMessage().getChatId();

    final var text = """
        📖 Справка по командам:
        
        /menu - Главное меню
        /profiles - Профиль
        /help - Это справка
        
        Используйте inline-кнопки для быстрого доступа к функциям.""";

    var messageReply = new SendMessage();
    messageReply.setChatId(chatId);
    messageReply.setText(text);

    try {
      bot.execute(messageReply);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }
}

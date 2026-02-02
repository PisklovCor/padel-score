package com.padelscore.telegram.handler.callback.player.profile;

import com.padelscore.telegram.handler.callback.Callback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Deprecated
@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackHelpPlayerProfile implements Callback {

  /**
   * Всегда false — callback не привязан (deprecated).
   */
  @Override
  public boolean coincidence(String command) {

    return false;
  }

  /**
   * Отправляет справку по командам профиля (/profiles, /create_profiles и т.д.).
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {

    final var chatId = callbackQuery.getMessage().getChatId();

    final var text = """
        📖 Справка по командам профиля:
        
        /profiles - Посмотреть профиль
        /create_profiles - Создать профиль
        /update_profiles - Обновить профиль
        /delete_profiles - Удалить профиль
        /help - Справка
        
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

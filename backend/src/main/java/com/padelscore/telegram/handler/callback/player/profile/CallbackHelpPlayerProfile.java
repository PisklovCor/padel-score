package com.padelscore.telegram.handler.callback.player.profile;

import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.util.TelegramExceptionHandler;
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

  public static final String PROFILE_COMMAND_REFERENCE = """
      📖 Справка по командам профиля:
      
      /profiles - Посмотреть профиль
      /create_profiles - Создать профиль
      /update_profiles - Обновить профиль
      /delete_profiles - Удалить профиль
      /help - Справка
      
      Используйте inline-кнопки для быстрого доступа к функциям.""";

  /**
   * Совпадение для команды «/help_profiles». Всегда false — callback не привязан (deprecated).
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

    var messageReply = new SendMessage();
    messageReply.setChatId(chatId);
    messageReply.setText(PROFILE_COMMAND_REFERENCE);

    try {
      bot.execute(messageReply);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

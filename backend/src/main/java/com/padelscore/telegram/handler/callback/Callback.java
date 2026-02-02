package com.padelscore.telegram.handler.callback;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface Callback {

  /**
   * Обрабатывает нажатие inline-кнопки и обновляет сообщение или отправляет ответ.
   *
   * @param callbackQuery данные нажатия кнопки (data, chatId, messageId)
   * @param bot           экземпляр бота для отправки ответа
   */
  void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot);

  /**
   * Проверяет, что callback data кнопки обрабатывается этим обработчиком.
   *
   * @param command callback data (например, "menu", "tournament_1")
   * @return true, если callback обрабатывается этим обработчиком
   */
  boolean coincidence(String command);
}

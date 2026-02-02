package com.padelscore.telegram.handler.command;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface Command {

  /**
   * Обрабатывает текстовую команду пользователя и отправляет ответ в чат.
   *
   * @param message входящее сообщение с командой
   * @param bot     экземпляр бота для отправки ответа
   */
  void handle(Message message, TelegramLongPollingBot bot);

  /**
   * Проверяет, что переданная строка совпадает с данной командой.
   *
   * @param command текст команды (например, "/start")
   * @return true, если команда обрабатывается этим обработчиком
   */
  boolean coincidence(String command);
}

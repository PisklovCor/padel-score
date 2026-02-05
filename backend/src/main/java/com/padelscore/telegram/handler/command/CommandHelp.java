package com.padelscore.telegram.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.telegram.util.KeyboardUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandHelp implements Command {

  /**
   * Совпадение для команды «/help».
   */
  @Override
  public boolean coincidence(String command) {

    return "/help".equals(command);
  }

  /**
   * Отправляет справку по командам (/menu, /profiles, /help).
   */
  @Override
  public void handle(Message message, TelegramLongPollingBot bot) {

    final var text = """
        📖 Справка по командам:
        
        /menu - Главное меню
        /profiles - Профиль
        /help - Это справка
        
        Используйте inline-кнопки для быстрого доступа к функциям.""";

    var messageReply = new SendMessage();
    messageReply.setChatId(message.getChatId().toString());
    messageReply.setText(text);

    try {
      bot.execute(messageReply);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }
}

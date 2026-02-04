package com.padelscore.telegram.handler.command.player.profile;

import com.padelscore.telegram.handler.command.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class CommandUpdatePlayerProfile implements Command {

  /**
   * Совпадение для команды «/update_profiles».
   */
  @Override
  public boolean coincidence(String command) {

    return "/update_profiles".equals(command);
  }

  /**
   * Отправляет сообщение о том, что обновление профиля пока не реализовано.
   */
  @Override
  public void handle(Message message, TelegramLongPollingBot bot) {

    var messageReply = new SendMessage();
    messageReply.setChatId(message.getChatId().toString());
    messageReply.setText("Пока не реализованно");

    try {
      bot.execute(messageReply);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }
}

package com.padelscore.telegram.handler.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.telegram.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandMenu implements Command {

  private final KeyboardUtil keyboardUtil;

  /**
   * Совпадение для команды /menu.
   */
  @Override
  public boolean coincidence(String command) {

    return "/menu".equals(command);
  }

  /**
   * Отправляет текст «Главное меню» и клавиатуру главного меню.
   */
  @Override
  public void handle(Message message, TelegramLongPollingBot bot) {

    var messageReply = new SendMessage();
    messageReply.setChatId(message.getChatId().toString());
    messageReply.setText("📑 Главное меню PadelScore Bot");
    messageReply.setReplyMarkup(keyboardUtil.getMenu());

    try {
      bot.execute(messageReply);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }
}

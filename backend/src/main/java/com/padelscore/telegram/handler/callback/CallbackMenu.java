package com.padelscore.telegram.handler.callback;

import com.padelscore.telegram.util.KeyboardUtil;
import com.padelscore.util.TelegramExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackMenu implements Callback {

  private final KeyboardUtil keyboardUtil;

  /**
   * Совпадение для callback data «menu».
   */
  @Override
  public boolean coincidence(String command) {

    return "menu".equals(command);
  }

  /**
   * Редактирует сообщение на текст «Главное меню» и клавиатуру главного меню.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {

    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();

    EditMessageText message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText("📑 Главное меню PadelScore Bot");
    message.setReplyMarkup(keyboardUtil.getMenu());

    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

package com.padelscore.telegram.handler.callback;

import com.padelscore.telegram.util.KeyboardUtil;
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
public class CallbackCommon implements Callback {

  private final KeyboardUtil keyboardUtil;

  @Override
  public boolean coincidence(String command) {

    return "main_menu".equals(command);
  }

  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {

    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();

    EditMessageText message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText("🏆 Главное меню PadelScore Bot");
    message.setReplyMarkup(keyboardUtil.getMainMenu());

    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }
}

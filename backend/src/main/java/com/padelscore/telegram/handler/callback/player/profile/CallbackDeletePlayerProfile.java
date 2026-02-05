package com.padelscore.telegram.handler.callback.player.profile;

import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardPlayerProfileUtil;
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
public class CallbackDeletePlayerProfile implements Callback {

  private final KeyboardPlayerProfileUtil keyboardPlayerProfileUtil;

  /**
   * Совпадение для callback data «delete_profiles».
   */
  @Override
  public boolean coincidence(String command) {
    return "delete_profiles".equals(command);
  }

  /**
   * Показывает предупреждение об удалении (рейтинг, членство в командах)
   * и кнопки подтверждения.
   */
  @Override
  public void handle(final CallbackQuery callbackQuery, final TelegramLongPollingBot bot) {
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();

    final var message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText(KeyboardPlayerProfileUtil.DELETE_PROFILE_WARNING);
    message.setReplyMarkup(keyboardPlayerProfileUtil.getDeleteConfirmKeyboard());

    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

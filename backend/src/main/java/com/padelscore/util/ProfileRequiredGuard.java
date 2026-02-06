package com.padelscore.util;

import com.padelscore.service.PlayerProfileService;
import com.padelscore.telegram.util.KeyboardPlayerProfileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Проверка наличия профиля пользователя. При отсутствии профиля отправляет/редактирует сообщение
 * с предложением создать профиль и клавиатурой с кнопкой «Создать профиль».
 */
@Component
@RequiredArgsConstructor
public class ProfileRequiredGuard {

  public static final String NO_PROFILE_MESSAGE =
      "⚠️ У вас пока нет профиля.\n\nВоспользуйтесь пунктом меню для создания профиля.";

  private final PlayerProfileService playerProfileService;

  private final KeyboardPlayerProfileUtil keyboardPlayerProfileUtil;

  /**
   * Для callback: при отсутствии профиля редактирует сообщение и возвращает {@code true}.
   * Иначе возвращает {@code false}.
   */
  public boolean requireProfileForCallback(Long userId, CallbackQuery callbackQuery,
      TelegramLongPollingBot bot) {
    return requireProfileForCallback(userId, callbackQuery, bot, NO_PROFILE_MESSAGE);
  }

  /**
   * То же с кастомным текстом сообщения (например, для рейтинга).
   */
  public boolean requireProfileForCallback(Long userId, CallbackQuery callbackQuery,
      TelegramLongPollingBot bot, String noProfileMessage) {
    if (playerProfileService.existsByTelegramId(userId)) {
      return false;
    }
    try {
      EditMessageText message = new EditMessageText();
      message.setChatId(callbackQuery.getMessage().getChatId().toString());
      message.setMessageId(callbackQuery.getMessage().getMessageId());
      message.setText(noProfileMessage);
      message.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(false));
      bot.execute(message);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
    return true;
  }

  /**
   * Для command: при отсутствии профиля отправляет сообщение в чат и возвращает {@code true}.
   * Иначе возвращает {@code false}.
   */
  public boolean requireProfileForCommand(Long userId, String chatId, TelegramLongPollingBot bot) {
    if (playerProfileService.existsByTelegramId(userId)) {
      return false;
    }
    try {
      bot.execute(MessageUtil.createdSendMessage(chatId, NO_PROFILE_MESSAGE,
          keyboardPlayerProfileUtil.getProfileMenu(false)));
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
    return true;
  }
}

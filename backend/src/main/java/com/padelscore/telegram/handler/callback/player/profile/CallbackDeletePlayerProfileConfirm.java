package com.padelscore.telegram.handler.callback.player.profile;

import com.padelscore.service.PlayerProfileService;
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
public class CallbackDeletePlayerProfileConfirm implements Callback {

  private final PlayerProfileService playerProfileService;

  private final KeyboardPlayerProfileUtil keyboardPlayerProfileUtil;

  /**
   * Совпадение для callback data «delete_profiles_confirm».
   */
  @Override
  public boolean coincidence(final String command) {
    return "delete_profiles_confirm".equals(command);
  }

  /**
   * Удаляет профиль пользователя и редактирует сообщение с подтверждением
   * и клавиатурой без профиля.
   */
  @Override
  public void handle(final CallbackQuery callbackQuery, final TelegramLongPollingBot bot) {
    final long userId = callbackQuery.getFrom().getId();
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();

    var playerProfileDto = playerProfileService.getPlayerProfileByTelegramId(userId);
    playerProfileService.deletePlayerProfile(playerProfileDto.getId());

    final var message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText("❌ Ваш профиль удален.");
    message.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(false));

    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

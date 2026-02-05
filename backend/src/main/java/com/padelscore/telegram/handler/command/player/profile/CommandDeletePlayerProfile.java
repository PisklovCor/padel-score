package com.padelscore.telegram.handler.command.player.profile;

import com.padelscore.service.PlayerProfileService;
import com.padelscore.telegram.handler.command.Command;
import com.padelscore.telegram.util.KeyboardPlayerProfileUtil;
import com.padelscore.util.TelegramExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandDeletePlayerProfile implements Command {

  private final PlayerProfileService playerProfileService;

  private final KeyboardPlayerProfileUtil keyboardPlayerProfileUtil;

  /**
   * Совпадение для команды «/delete_profiles».
   */
  @Override
  public boolean coincidence(String command) {
    return "/delete_profiles".equals(command);
  }

  /**
   * Показывает предупреждение об удалении (рейтинг, членство в командах)
   * и кнопки подтверждения.
   */
  @Override
  public void handle(Message message, TelegramLongPollingBot bot) {
    final long userId = message.getFrom().getId();
    if (!playerProfileService.existsByTelegramId(userId)) {
      var noProfile = new SendMessage();
      noProfile.setChatId(message.getChatId().toString());
      noProfile.setText("⚠️ У вас пока нет профиля.");
      noProfile.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(false));
      try {
        bot.execute(noProfile);
      } catch (TelegramApiException e) {
        TelegramExceptionHandler.handle(e);
      }
      return;
    }

    var messageReply = new SendMessage();
    messageReply.setChatId(message.getChatId().toString());
    messageReply.setText(KeyboardPlayerProfileUtil.DELETE_PROFILE_WARNING);
    messageReply.setReplyMarkup(
        keyboardPlayerProfileUtil.getDeleteConfirmKeyboard());

    try {
      bot.execute(messageReply);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

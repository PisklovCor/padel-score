package com.padelscore.telegram.handler.command.player.profile;

import com.padelscore.telegram.handler.command.Command;
import com.padelscore.telegram.util.KeyboardPlayerProfileUtil;
import com.padelscore.util.ProfileRequiredGuard;
import com.padelscore.util.MessageUtil;
import com.padelscore.util.TelegramExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandDeletePlayerProfile implements Command {

  private final ProfileRequiredGuard profileRequiredGuard;

  private final KeyboardPlayerProfileUtil keyboardPlayerProfileUtil;

  /**
   * Совпадение для команды «/delete_profiles».
   */
  @Override
  public boolean coincidence(String command) {
    return "/delete_profiles".equals(command);
  }

  /**
   * Показывает предупреждение об удалении (рейтинг, членство в командах) и кнопки подтверждения.
   */
  @Override
  public void handle(Message message, TelegramLongPollingBot bot) {
    final long userId = message.getFrom().getId();
    final var chatId = message.getChatId().toString();

    if (profileRequiredGuard.requireProfileForCommand(userId, chatId, bot)) {
      return;
    }

    try {
      bot.execute(
          MessageUtil.createdSendMessage(chatId, KeyboardPlayerProfileUtil.DELETE_PROFILE_WARNING,
              keyboardPlayerProfileUtil.getProfileMenu(false)));
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

package com.padelscore.telegram.handler.callback.player.profile;

import com.padelscore.service.PlayerProfileService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardPlayerProfileUtil;
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
public class CallbackPlayerProfile implements Callback {

  private final PlayerProfileService playerProfileService;

  private final KeyboardPlayerProfileUtil keyboardPlayerProfileUtil;

  /**
   * Совпадение для callback data «profiles».
   */
  @Override
  public boolean coincidence(String command) {

    return "profiles".equals(command);
  }

  /**
   * Редактирует сообщение: данные профиля пользователя или предложение создать профиль и
   * клавиатуру.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {

    final long userId = callbackQuery.getFrom().getId();
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    String text;
    final boolean isProfileExists = playerProfileService.existsByTelegramId(userId);

    if (isProfileExists) {

      final var playerProfileDto = playerProfileService.getPlayerProfileByTelegramId(userId);

      text = """
          👤 Профиль пользователя:
          
          Ник - %s
          Имя - %s
          Рейтинг - %d""".formatted(
          playerProfileDto.getNickname(),
          playerProfileDto.getFirstName(),
          playerProfileDto.getRating());
    } else {
      text = """
          ⚠️ У вас пока нет профиля:
          
          Для быстрого создания используйте кнопку.""";
    }

    final var message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(callbackQuery.getMessage().getMessageId());
    message.setText(text);
    message.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(isProfileExists));

    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }
}

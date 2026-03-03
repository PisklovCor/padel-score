package com.padelscore.telegram.handler.callback.player.profile;

import com.padelscore.dto.PlayerProfileDto;
import com.padelscore.service.PlayerProfileService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardPlayerProfileUtil;
import com.padelscore.util.MessageUtil;
import com.padelscore.util.TelegramExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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
    final var userId = callbackQuery.getFrom().getId();
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    String text;
    final var isProfileExists = playerProfileService.existsByTelegramId(userId);

    if (isProfileExists) {

      final var playerProfileDto = playerProfileService.getPlayerProfileByTelegramId(userId);
      text = createsDescriptionForProfile(playerProfileDto);
    } else {
      text = """
          ⚠️ У вас пока нет профиля:
          
          Для быстрого создания используйте кнопку.""";
    }

    try {
      bot.execute(
          MessageUtil.createdEditMessageText(chatId, callbackQuery.getMessage().getMessageId(),
              text, keyboardPlayerProfileUtil.getProfileMenu(isProfileExists)));
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }

  private String createsDescriptionForProfile(PlayerProfileDto playerProfileDto) {
    return String.format(
        "👤 Профиль пользователя:%n%nНик - %s%nИмя - %s%nРейтинг - %d",
        playerProfileDto.getNickname(),
        playerProfileDto.getFirstName(),
        playerProfileDto.getRating());
  }
}

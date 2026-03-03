package com.padelscore.telegram.handler.command.player.profile;

import com.padelscore.dto.PlayerProfileDto;
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
public class CommandPlayerProfile implements Command {

  private final PlayerProfileService playerProfileService;

  private final KeyboardPlayerProfileUtil keyboardPlayerProfileUtil;

  /**
   * Совпадение для команды «/profiles».
   */
  @Override
  public boolean coincidence(String command) {

    return "/profiles".equals(command);
  }

  /**
   * Отправляет данные профиля пользователя или предложение создать профиль и клавиатуру профиля.
   */
  @Override
  public void handle(Message message, TelegramLongPollingBot bot) {

    final long userId = message.getFrom().getId();
    String text;
    final boolean isProfileExists = playerProfileService.existsByTelegramId(userId);

    if (isProfileExists) {
      final var playerProfileDto = playerProfileService.getPlayerProfileByTelegramId(userId);
      text = getTextProfileExists(playerProfileDto);
    } else {
      text = getGetTextProfileNotExists();
    }

    var messageReply = new SendMessage();
    messageReply.setChatId(message.getChatId().toString());
    messageReply.setText(text);
    messageReply.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(isProfileExists));

    try {
      bot.execute(messageReply);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }

  private String getTextProfileExists(PlayerProfileDto playerProfileDto) {
    return String.format(
        "👤 Профиль пользователя:%n%nНик - %s%nИмя - %s%nРейтинг - %d",
        playerProfileDto.getNickname(),
        playerProfileDto.getFirstName(),
        playerProfileDto.getRating());
  }

  private String getGetTextProfileNotExists() {
    return """
        ⚠️ У вас пока нет профиля:
        
        Для быстрого создания используйте кнопку.""";
  }
}

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
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackCreatePlayerProfile implements Callback {

  private final PlayerProfileService playerProfileService;

  private final KeyboardPlayerProfileUtil keyboardPlayerProfileUtil;

  @Override
  public boolean coincidence(String command) {

    return "create_profiles".equals(command);
  }

  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {

    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final User user = callbackQuery.getFrom();
    final String nickname = user.getUserName();

    if (nickname == null || nickname.isBlank()) {
      throw new IllegalArgumentException(
          "У вас не заполнен ник в телеграмме. Воспользуйтесь командой: /create_profiles Сахарок");
    }

    log.info("UserId=[{}], nickname=[{}]", user.getId(), nickname);

    final var playerProfileDto = playerProfileService.createPlayerProfile(user.getFirstName(),
        user.getLastName(), nickname, user.getId(), 0);

    final var text = """
        ✅ Профиль создан:
        
        Ник - %s
        Имя - %s
        Рейтинг - %d""".formatted(
        playerProfileDto.getNickname(),
        playerProfileDto.getFirstName(),
        playerProfileDto.getRating());

    final var message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(callbackQuery.getMessage().getMessageId());
    message.setText(text);
    message.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(true));

    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }
}

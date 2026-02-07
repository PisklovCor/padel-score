package com.padelscore.telegram.handler.callback.player.profile;

import com.padelscore.exception.NicknameNotUniqueException;
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
public class CallbackCreatePlayerProfile implements Callback {

  private final PlayerProfileService playerProfileService;

  private final KeyboardPlayerProfileUtil keyboardPlayerProfileUtil;

  /**
   * Совпадение для callback data «create_profiles».
   */
  @Override
  public boolean coincidence(String command) {

    return "create_profiles".equals(command);
  }

  /**
   * Создаёт профиль по данным Telegram (ник обязателен) и редактирует сообщение с подтверждением.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {

    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var user = callbackQuery.getFrom();
    final var nickname = user.getUserName();

    if (nickname == null || nickname.isBlank()) {
      throw new IllegalArgumentException(
          "У вас не заполнен ник в телеграмме. Воспользуйтесь командой: /create_profiles Сахарок");
    }

    log.info("UserId=[{}], nickname=[{}]", user.getId(), nickname);

    final var editMessage = new EditMessageText();
    editMessage.setChatId(chatId);
    editMessage.setMessageId(callbackQuery.getMessage().getMessageId());

    try {
      final var dto = playerProfileService.createPlayerProfile(user.getFirstName(),
          user.getLastName(), nickname, user.getId(), null);
      setProfileCreatedText(editMessage, dto.getNickname(), dto.getFirstName(), dto.getRating());
      editMessage.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(true));
      bot.execute(editMessage);
    } catch (NicknameNotUniqueException e) {
      editMessage.setText("Ник уже занят. Введите ник вручную командой: /create_profiles ник");
      editMessage.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(false));
      try {
        bot.execute(editMessage);
      } catch (TelegramApiException ex) {
        TelegramExceptionHandler.handle(e);
      }
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }

  private void setProfileCreatedText(EditMessageText msg, String nick, String firstName, int rating) {
    msg.setText("""
        ✅ Профиль создан:
        
        Ник - %s
        Имя - %s
        Рейтинг - %d""".formatted(nick, firstName, rating));
  }
}

package com.padelscore.telegram.handler.command.player.profile;

import com.padelscore.dto.CreatePlayerProfileRequest;
import com.padelscore.exception.NicknameNotUniqueException;
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
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandCreatePlayerProfile implements Command {

  private final PlayerProfileService playerProfileService;

  private final KeyboardPlayerProfileUtil keyboardPlayerProfileUtil;

  /**
   * Совпадение для команды «/create_profiles» (с опциональным ником).
   */
  @Override
  public boolean coincidence(String command) {

    final String[] parts = command.trim().split("\\s+", 2);
    return "/create_profiles".equals(parts[0]);
  }

  /**
   * Создаёт профиль по данным Telegram (или нику из текста) и отправляет подтверждение с
   * клавиатурой.
   */
  @Override
  public void handle(Message message, TelegramLongPollingBot bot) {
    final User user = message.getFrom();
    final String nickname = resolveNickname(user, message.getText().trim());
    log.info("UserId=[{}], nickname=[{}]", user.getId(), nickname);
    final String chatId = message.getChatId().toString();
    try {
      createAndSendProfile(user, nickname, chatId, bot);
    } catch (NicknameNotUniqueException e) {
      sendNicknameTakenHint(chatId, bot);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }

  private String resolveNickname(User user, String input) {
    String nickname = user.getUserName();
    return (nickname == null || nickname.isBlank()) ? commandCheck(input) : nickname;
  }

  private void createAndSendProfile(User user, String nickname, String chatId,
      TelegramLongPollingBot bot) throws TelegramApiException {
    final var req = CreatePlayerProfileRequest.builder()
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .nickname(nickname)
        .telegramId(user.getId())
        .rating(null)
        .build();
    final var dto = playerProfileService.createPlayerProfile(req);
    sendProfileCreated(chatId, dto.getNickname(), dto.getFirstName(), dto.getRating(), bot);
  }

  private String commandCheck(String commandInput) {
    String[] parts = commandInput.trim().split("\\s+", 2);

    if (parts.length < 2) {
      throw new IllegalArgumentException(
          "После команды должен быть ник т.к. у вас он не заполнен в телеграмме." +
              " Пример: /create_profiles Сахарок");
    }

    final String rest = parts[1];

    String[] args = rest.trim().split("\\s+");
    if (args.length != 1) {
      throw new IllegalArgumentException("После команды должен быть ник в одно слово");
    }

    return args[0];
  }


  private void sendProfileCreated(String chatId, String nick, String firstName, int rating,
                                  TelegramLongPollingBot bot) throws TelegramApiException {
    final var text = String.format(
        "✅ Профиль создан:%n%nНик - %s%nИмя - %s%nРейтинг - %d",
        nick, firstName, rating);
    var msg = new SendMessage();
    msg.setChatId(chatId);
    msg.setText(text);
    msg.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(true));
    bot.execute(msg);
  }

  private void sendNicknameTakenHint(String chatId, TelegramLongPollingBot bot) {
    var msg = new SendMessage();
    msg.setChatId(chatId);
    msg.setText("Ник уже занят. Введите ник вручную командой: /create_profiles ник");
    msg.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(false));
    try {
      bot.execute(msg);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

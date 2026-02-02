package com.padelscore.telegram.handler.command.player.profile;

import com.padelscore.service.PlayerProfileService;
import com.padelscore.telegram.handler.command.Command;
import com.padelscore.telegram.util.KeyboardPlayerProfileUtil;
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
   * Совпадение для команды /create_profiles (с опциональным ником).
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
    final String messageText = message.getText();
    final String input = messageText.trim();
    String nickname = user.getUserName();

    if (nickname == null || nickname.isBlank()) {
      String[] parts = input.trim().split("\\s+", 2);

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

      nickname = args[0];

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

    var messageReply = new SendMessage();
    messageReply.setChatId(message.getChatId().toString());
    messageReply.setText(text);
    messageReply.setReplyMarkup(keyboardPlayerProfileUtil.getProfileMenu(true));

    try {
      bot.execute(messageReply);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }
}

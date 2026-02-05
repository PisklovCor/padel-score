package com.padelscore.telegram.handler.command;

import com.padelscore.config.PropertiesConfiguration;
import com.padelscore.service.MatchReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
public class CommandReminders implements Command {

  private final MatchReminderService matchReminderService;

  private final PropertiesConfiguration propertiesConfiguration;

  public CommandReminders(@Lazy MatchReminderService matchReminderService,
      PropertiesConfiguration propertiesConfiguration) {
    this.matchReminderService = matchReminderService;
    this.propertiesConfiguration = propertiesConfiguration;
  }

  /**
   * Совпадение для команды «/reminders».
   */
  @Override
  public boolean coincidence(String command) {
    return "/reminders".equals(command);
  }

  /**
   * Запускает отправку напоминаний о матчах на завтра. Доступна только администратору бота.
   */
  @Override
  public void handle(Message message, TelegramLongPollingBot bot) {
    final Long userId = message.getFrom().getId();

    final String botAdminId = propertiesConfiguration.getBotAdminId();

    if (checkingAdministratorRights(message, userId, botAdminId, bot)) {
      return;
    }

    try {
      matchReminderService.sendTomorrowMatchReminders();

      var messageReply = new SendMessage();
      messageReply.setChatId(message.getChatId().toString());
      messageReply.setText("✅ Напоминания о матчах успешно отправлены");

      bot.execute(messageReply);
    } catch (TelegramApiException e) {
      log.error("Ошибка при отправке ответа на команду /reminders: {}", e.getMessage(), e);
    } catch (Exception e) {
      log.error("Ошибка при выполнении команды /reminders: {}", e.getMessage(), e);
      try {
        var errorReply = new SendMessage();
        errorReply.setChatId(message.getChatId().toString());
        errorReply.setText("❌ Произошла ошибка при отправке напоминаний");
        bot.execute(errorReply);
      } catch (TelegramApiException ex) {
        log.error("Не удалось отправить сообщение об ошибке: {}", ex.getMessage(), ex);
      }
    }
  }

  private boolean checkingAdministratorRights(Message message, Long userId, String botAdminId,
      TelegramLongPollingBot bot) {

    if (botAdminId == null || botAdminId.isEmpty()) {
      log.warn("BOT_ADMIN не настроен, команда /reminders недоступна");
      sendAccessDeniedMessage(message.getChatId(), bot);
      return true;
    }

    try {
      Long adminId = Long.parseLong(botAdminId);
      if (!adminId.equals(userId)) {
        log.warn("Попытка выполнения команды /reminders пользователем {} (разрешен только {})",
            userId, adminId);
        sendAccessDeniedMessage(message.getChatId(), bot);
        return true;
      }
    } catch (NumberFormatException e) {
      log.error("Неверный формат BOT_ADMIN: {}", botAdminId);
      sendAccessDeniedMessage(message.getChatId(), bot);
      return true;
    }

    return false;
  }

  private void sendAccessDeniedMessage(Long chatId, TelegramLongPollingBot bot) {
    try {
      var messageReply = new SendMessage();
      messageReply.setChatId(chatId.toString());
      messageReply.setText("❌ У вас нет прав для выполнения этой команды");
      bot.execute(messageReply);
    } catch (TelegramApiException e) {
      log.error("Не удалось отправить сообщение об отказе в доступе: {}", e.getMessage(), e);
    }
  }
}

package com.padelscore.telegram;

import com.padelscore.telegram.handler.CommandHandler;
import com.padelscore.telegram.handler.CallbackHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PadelScoreBot extends TelegramLongPollingBot {

  @Value("${telegram.bot.token}")
  private String botToken;

  @Value("${telegram.bot.username}")
  private String botUsername;

  private final CommandHandler commandHandler;

  private final CallbackHandler callbackHandler;

  @Override
  public String getBotUsername() {
    return botUsername;
  }

  @Override
  public String getBotToken() {
    return botToken;
  }

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      commandHandler.handle(update.getMessage(), this);
    } else if (update.hasCallbackQuery()) {
      callbackHandler.handle(update.getCallbackQuery(), this);
    }
  }

  @PostConstruct
  public void initBotCommands() {
    List<BotCommand> commands = List.of(
        new BotCommand("/start", "Главное меню"),
        new BotCommand("/profiles", "Профиль"),
        new BotCommand("/help", "Справка")
    );

    SetMyCommands setMyCommands = new SetMyCommands();
    setMyCommands.setCommands(commands);
    setMyCommands.setScope(new BotCommandScopeDefault());
    setMyCommands.setLanguageCode("ru");

    try {
      execute(setMyCommands);
      log.info("Команды бота успешно установлены в меню");
    } catch (TelegramApiException e) {
      log.error("Ошибка при установке команд бота: {}", e.getMessage(), e);
    }
  }
}

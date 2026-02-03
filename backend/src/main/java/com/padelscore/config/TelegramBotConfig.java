package com.padelscore.config;

import com.padelscore.telegram.PadelScoreBot;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@ConditionalOnProperty(prefix = "telegrambots", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class TelegramBotConfig {

  private final PadelScoreBot padelScoreBot;

  /**
   * Создаёт и регистрирует API ботов Telegram, регистрирует PadelScoreBot.
   */
  @Bean
  public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
    botsApi.registerBot(padelScoreBot);
    return botsApi;
  }
}

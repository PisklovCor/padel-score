package com.padelscore.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TelegramExceptionHandler {

  /**
   * Логирует ошибку Telegram API без указания контекста.
   *
   * @param e исключение, возникшее при обращении к Telegram API
   */
  public void handle(Exception e) {
    log.error("Ошибка Telegram API: {}", e.getMessage(), e);
    throw new RuntimeException("Ошибка Telegram API: " + e.getMessage(), e);
  }

  /**
   * Логирует ошибку в Telegram API с контекстом и пробрасывает как {@link RuntimeException}.
   *
   * @param e       исходное исключение
   * @param context описание контекста (например, "EditMessageText", "SendMessage")
   */
  public void handle(Exception e, String context) {
    log.error("Ошибка Telegram API ({}): {}", context, e.getMessage(), e);
    throw new RuntimeException("Ошибка Telegram API: " + e.getMessage(), e);
  }

  /**
   * Только логирует ошибку без проброса, например, для нефатальных ситуаций.
   *
   * @param e       исходное исключение
   * @param context описание контекста
   */
  public void logOnly(Exception e, String context) {
    log.warn("Предупреждение Telegram API ({}): {}", context, e.getMessage(), e);
  }
}

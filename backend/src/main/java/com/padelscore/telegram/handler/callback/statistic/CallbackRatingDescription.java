package com.padelscore.telegram.handler.callback.statistic;

import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardUtil;
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
public class CallbackRatingDescription implements Callback {

  private static final String DESCRIPTION = """
      📋 Как считается рейтинг

      • Стартовый рейтинг — 500.
      • Рейтинг команды = среднее рейтингов двух игроков.
      • После каждого матча по формуле Elo считается ожидаемый результат (E) и сравнивается с фактическим (победа 2:0 или 2:1).
      • Коэффициент K = 24. Изменение рейтинга команды распределяется поровну между игроками.
      """;

  private final KeyboardUtil keyboardUtil;

  @Override
  public boolean coincidence(String command) {
    return KeyboardUtil.RATING_DESCRIPTION.equals(command);
  }

  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String chatId = callbackQuery.getMessage().getChatId().toString();
    int messageId = callbackQuery.getMessage().getMessageId();
    try {
      bot.execute(MessageUtil.createdEditMessageText(
          chatId, messageId, DESCRIPTION, keyboardUtil.getRatingDescriptionBackKeyboard()));
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

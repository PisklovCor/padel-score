package com.padelscore.telegram.handler.callback.statistic;

import com.padelscore.dto.PlayerProfileDto;
import com.padelscore.service.StatisticsService;
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

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackPlayerRating implements Callback {

  private static final int TOP_SIZE = 10;

  private final StatisticsService statisticsService;

  private final KeyboardUtil keyboardUtil;

  @Override
  public boolean coincidence(String command) {
    return "player_rating".equals(command);
  }

  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String chatId = callbackQuery.getMessage().getChatId().toString();
    int messageId = callbackQuery.getMessage().getMessageId();

    List<PlayerProfileDto> top = statisticsService.getTopPlayersByRating(TOP_SIZE);
    String text = formatTopPlayers(top);

    try {
      bot.execute(
          MessageUtil.createdEditMessageText(chatId, messageId, text, keyboardUtil.getButtonToMenu()));
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }

  private String formatTopPlayers(List<PlayerProfileDto> top) {
    StringBuilder sb = new StringBuilder("📊 Рейтинг игроков (топ ").append(TOP_SIZE)
        .append(")\n\n");
    if (top.isEmpty()) {
      sb.append("Пока нет игроков с указанным рейтингом.");
      return sb.toString();
    }
    int pos = 1;
    for (PlayerProfileDto p : top) {
      String name = (p.getNickname() != null ? p.getNickname() : p.getFirstName())
          + (p.getLastName() != null && !p.getLastName().isBlank() ? " " + p.getLastName() : "");
      sb.append(pos).append(". ").append(name).append(" — ").append(p.getRating()).append("\n");
      pos++;
    }
    return sb.toString();
  }
}

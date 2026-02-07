package com.padelscore.telegram.handler.callback.statistic;

import com.padelscore.dto.PlayerProfileDto;
import com.padelscore.service.StatisticsService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardStatistic;
import com.padelscore.util.ProfileRequiredGuard;
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

  private static final String NO_PROFILE_MESSAGE =
      "⚠️ У вас пока нет профиля.\n\nПросмотр рейтинга недоступен. Воспользуйтесь пунктом меню для создания профиля.";

  private final StatisticsService statisticsService;

  private final ProfileRequiredGuard profileRequiredGuard;

  private final KeyboardStatistic keyboardStatistic;

  @Override
  public boolean coincidence(String command) {
    return "player_rating".equals(command);
  }

  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String chatId = callbackQuery.getMessage().getChatId().toString();
    int messageId = callbackQuery.getMessage().getMessageId();
    Long userId = callbackQuery.getFrom().getId();

    try {
      if (profileRequiredGuard.requireProfileForCallback(userId, callbackQuery, bot,
          NO_PROFILE_MESSAGE)) {
        return;
      }
      List<PlayerProfileDto> top = statisticsService.getTopPlayersByRating(TOP_SIZE);
      String text = formatTopPlayers(top);
      bot.execute(
          MessageUtil.createdEditMessageText(chatId, messageId, text,
              keyboardStatistic.getPlayerRatingKeyboard()));
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
      String name = p.getNickname() + " " + p.getFirstName();
      sb.append(pos).append(". ").append(name).append(" — ").append(p.getRating()).append("\n");
      pos++;
    }
    return sb.toString();
  }
}

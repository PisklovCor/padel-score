package com.padelscore.telegram.handler.callback.match;

import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.dto.MatchDto;
import com.padelscore.service.MatchService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardMatchUtil;
import com.padelscore.util.MessageUtil;
import com.padelscore.util.TelegramExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackMatchesList implements Callback {

  private final MatchService matchService;

  private final KeyboardMatchUtil keyboardMatchUtil;

  /**
   * Совпадение для callback data «matches_list_<tournamentId>».
   */
  @Override
  public boolean coincidence(String command) {
    return command != null && command.startsWith("matches_list_");
  }

  /**
   * Список матчей турнира и клавиатуру с матчами.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    final var data = callbackQuery.getData();
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();
    String messageText = "";

    try {
      Integer tournamentId = Integer.parseInt(data.split("_")[2]);
      List<MatchDto> matches = matchService.getMatchesByTournament(tournamentId);

      if (matches.isEmpty()) {
        messageText =
            "⚽ Матчи турнира\n\nВ этом турнире пока нет матчей.\n\nИспользуйте кнопку ниже, чтобы создать матч.";
      } else {
        messageText = createListMatches(matches);
      }
      bot.execute(MessageUtil.createdEditMessageText(chatId, messageId, messageText,
          keyboardMatchUtil.getMatchesMenu(matches, tournamentId)));
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }

  private String createListMatches(List<MatchDto> matches) {
    StringBuilder text = new StringBuilder("⚽ Матчи турнира\n\n");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    for (MatchDto match : matches) {
      String status = "SCHEDULED".equals(match.getStatus()) ? "⏰"
          : "COMPLETED".equals(match.getStatus()) ? "✅" : "🔄";
      String teams = match.getTeam1Name() + " vs " + match.getTeam2Name();
      String dateStr = match.getScheduledDate() != null
          ? match.getScheduledDate().format(dateFormatter) : "—";
      String location = match.getLocation() != null && !match.getLocation().isBlank()
          ? match.getLocation().trim() : "—";
      text.append(String.format("%s %s — %s — %s\n", status, teams, dateStr, location));
    }

    return text.toString();
  }
}

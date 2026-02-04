package com.padelscore.telegram.handler.callback.match;

import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.dto.MatchDto;
import com.padelscore.service.MatchService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardTournamentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackMatchesList implements Callback {

  private final MatchService matchService;

  private final KeyboardTournamentUtil keyboardTournamentUtil;

  /**
   * Совпадение для callback data «matches_list_<tournamentId>».
   */
  @Override
  public boolean coincidence(String command) {
    return command != null && command.startsWith("matches_list_");
  }

  /**
   * Редактирует сообщение: список матчей турнира и клавиатуру с матчами.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String data = callbackQuery.getData();
    String chatId = callbackQuery.getMessage().getChatId().toString();
    Integer messageId = callbackQuery.getMessage().getMessageId();

    try {
      Integer tournamentId = Integer.parseInt(data.split("_")[2]);
      List<MatchDto> matches = matchService.getMatchesByTournament(tournamentId);

      EditMessageText message = new EditMessageText();
      message.setChatId(chatId);
      message.setMessageId(messageId);

      if (matches.isEmpty()) {
        message.setText(
            "⚽ Матчи турнира\n\nВ этом турнире пока нет матчей.\n\nИспользуйте кнопку ниже, чтобы создать матч.");
      } else {
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
        message.setText(text.toString());
      }
      message.setReplyMarkup(keyboardTournamentUtil.getMatchesMenu(matches, tournamentId));
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
    }
  }
}

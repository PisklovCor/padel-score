package com.padelscore.telegram.handler.callback.tournament;

import com.padelscore.dto.LeaderboardEntryDto;
import com.padelscore.service.StatisticsService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardTournamentUtil;
import com.padelscore.util.TelegramExceptionHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackLeaderboard implements Callback {

  private static final int TEAM_NAME_MAX_LEN = 20;

  private final StatisticsService statisticsService;

  private final KeyboardTournamentUtil keyboardTournamentUtil;

  @Override
  public boolean coincidence(String command) {
    return command != null
        && (command.startsWith(KeyboardTournamentUtil.LEADERBOARD)
        || command.startsWith(KeyboardTournamentUtil.LEADERBOARD_FULL));
  }

  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    final var data = callbackQuery.getData();
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();

    try {
      boolean isFull = data.startsWith(KeyboardTournamentUtil.LEADERBOARD_FULL);
      String prefix = isFull ? KeyboardTournamentUtil.LEADERBOARD_FULL
          : KeyboardTournamentUtil.LEADERBOARD;
      Integer tournamentId = Integer.parseInt(data.substring(prefix.length()));
      List<LeaderboardEntryDto> leaderboard = statisticsService.getLeaderboard(tournamentId);

      EditMessageText message = buildEditMessage(chatId, messageId, tournamentId, leaderboard,
          isFull);
      bot.execute(message);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }

  private EditMessageText buildEditMessage(String chatId, int messageId, Integer tournamentId,
      List<LeaderboardEntryDto> leaderboard, boolean isFull) {
    String text = isFull ? formatFullTable(leaderboard) : formatCompactTable(leaderboard);
    var markup = isFull ? keyboardTournamentUtil.getLeaderboardFullKeyboard(tournamentId)
        : keyboardTournamentUtil.getLeaderboardKeyboard(tournamentId);
    EditMessageText message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText(text);
    message.setReplyMarkup(markup);
    return message;
  }

  private String formatCompactTable(List<LeaderboardEntryDto> leaderboard) {
    StringBuilder sb = new StringBuilder("📊 Турнирная таблица\n\n");
    if (leaderboard.isEmpty()) {
      sb.append("В турнире пока нет команд или сыгранных матчей.");
      return sb.toString();
    }
    int pos = 1;
    for (LeaderboardEntryDto e : leaderboard) {
      String medal = pos <= 3 ? getMedal(pos) + " " : "   ";
      int wr = (e.getMatches() > 0 && e.getWinRate() != null)
          ? (int) (e.getWinRate() * 100) : 0;
      sb.append(String.format("%s%d. %-20s  И:%d  В:%d  П:%d  |  Очки %2d  |  WR %d%%%n",
          medal, pos, truncate(e.getTeamName(), TEAM_NAME_MAX_LEN),
          e.getMatches(), e.getWins(), e.getLosses(), e.getPoints(), wr));
      pos++;
    }
    return sb.toString();
  }

  private String formatFullTable(List<LeaderboardEntryDto> leaderboard) {
    StringBuilder sb = new StringBuilder("📊 Турнирная таблица (подробно)\n\n");
    if (leaderboard.isEmpty()) {
      sb.append("В турнире пока нет команд или сыгранных матчей.");
      return sb.toString();
    }
    int pos = 1;
    for (LeaderboardEntryDto e : leaderboard) {
      String medal = pos <= 3 ? getMedal(pos) + " " : "   ";
      int wr = (e.getMatches() > 0 && e.getWinRate() != null)
          ? (int) (e.getWinRate() * 100) : 0;
      int setsDiff = e.getSetsWon() - e.getSetsLost();
      int gamesDiff = e.getGamesWon() - e.getGamesLost();
      sb.append(String.format("%s%d. %s%n", medal, pos, truncate(e.getTeamName(), TEAM_NAME_MAX_LEN)));
      sb.append(String.format("   Матчи: %d  |  В-П: %d-%d  |  Сеты: %d-%d (%+d)  |  Геймы: %d-%d (%+d)%n",
          e.getMatches(), e.getWins(), e.getLosses(),
          e.getSetsWon(), e.getSetsLost(), setsDiff,
          e.getGamesWon(), e.getGamesLost(), gamesDiff));
      sb.append(String.format("   Очки: %d  |  WR: %d%%%n%n", e.getPoints(), wr));
      pos++;
    }
    return sb.toString();
  }

  private String getMedal(int position) {
    return switch (position) {
      case 1 -> "🥇";
      case 2 -> "🥈";
      case 3 -> "🥉";
      default -> "";
    };
  }

  private String truncate(String name, int maxLen) {
    if (name == null) {
      return "";
    }
    return name.length() > maxLen ? name.substring(0, maxLen - 3) + "..." : name;
  }
}

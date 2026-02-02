package com.padelscore.telegram.handler.callback.tournament;

import com.padelscore.dto.MatchDto;
import com.padelscore.dto.MatchResultDto;
import com.padelscore.dto.TeamDto;
import com.padelscore.service.MatchService;
import com.padelscore.service.TeamService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardTournamentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackTournamentMatches implements Callback {

  private final MatchService matchService;
  private final TeamService teamService;
  private final KeyboardTournamentUtil keyboardTournamentUtil;

  /**
   * Совпадение для matches_list_, match_, match_result_, result_quick_, match_view_,
   * match_dispute_, match_create_.
   */
  @Override
  public boolean coincidence(String command) {
    return command != null
        && (command.startsWith("matches_list_")
        || command.startsWith("match_")
        || command.startsWith("result_quick_"));
  }

  /**
   * Обрабатывает матчи турнира: список матчей, карточка матча, ввод результата, просмотр,
   * оспаривание, создание.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String data = callbackQuery.getData();
    String chatId = callbackQuery.getMessage().getChatId().toString();
    Integer messageId = callbackQuery.getMessage().getMessageId();
    Long userId = callbackQuery.getFrom().getId();

    try {
      if (data.startsWith("matches_list_")) {
        handleMatchesList(data, chatId, messageId, bot);
      } else if (data.startsWith("match_result_")) {
        handleMatchResultInput(data, chatId, messageId, bot);
      } else if (data.startsWith("result_quick_")) {
        handleQuickResult(data, chatId, messageId, userId, bot);
      } else if (data.startsWith("match_view_")) {
        handleMatchView(data, chatId, messageId, bot);
      } else if (data.startsWith("match_dispute_")) {
        handleMatchDispute(data, chatId, messageId, userId, bot);
      } else if (data.startsWith("match_create_")) {
        handleMatchCreate(data, chatId, userId, bot);
      } else if (data.startsWith("match_")) {
        handleMatchCard(data, chatId, messageId, bot);
      }
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
    }
  }

  private void handleMatchesList(String data, String chatId, Integer messageId,
      TelegramLongPollingBot bot) throws TelegramApiException {
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
  }

  private void handleMatchCard(String data, String chatId, Integer messageId,
      TelegramLongPollingBot bot) throws TelegramApiException {
    String[] parts = data.split("_");
    if (parts.length != 2) {
      return;
    }
    Integer matchId = Integer.parseInt(parts[1]);
    MatchDto match = matchService.getMatch(matchId);

    StringBuilder text = new StringBuilder("⚽ Матч: ")
        .append(match.getTeam1Name())
        .append(" vs ")
        .append(match.getTeam2Name())
        .append("\n\n");
    text.append("ID: ").append(match.getId()).append("\n");
    text.append("Статус: ").append(match.getStatus()).append("\n");
    text.append("Формат: ").append(match.getFormat()).append("\n");
    if (match.getScheduledDate() != null) {
      text.append("Дата: ")
          .append(match.getScheduledDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
          .append("\n");
    }
    if (match.getLocation() != null && !match.getLocation().isBlank()) {
      text.append("Место: ").append(match.getLocation().trim()).append("\n");
    }

    EditMessageText message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText(text.toString());
    message.setReplyMarkup(keyboardTournamentUtil.getMatchMenu(
        matchId, match.getTournamentId(), match.getStatus()));
    bot.execute(message);
  }

  private void handleMatchResultInput(String data, String chatId, Integer messageId,
      TelegramLongPollingBot bot) throws TelegramApiException {
    Integer matchId = Integer.parseInt(data.split("_")[2]);
    MatchDto match = matchService.getMatch(matchId);

    EditMessageText message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText("Выберите результат матча:\n\n"
        + match.getTeam1Name() + " vs " + match.getTeam2Name());
    message.setReplyMarkup(keyboardTournamentUtil.getResultInputMenu(matchId));
    bot.execute(message);
  }

  private void handleQuickResult(String data, String chatId, Integer messageId, Long userId,
      TelegramLongPollingBot bot) throws TelegramApiException {
    String[] parts = data.split("_");
    Integer matchId = Integer.parseInt(parts[2]);
    String score = parts[3];

    try {
      matchService.submitResult(matchId, score, userId, null);
      MatchDto match = matchService.getMatch(matchId);

      EditMessageText message = new EditMessageText();
      message.setChatId(chatId);
      message.setMessageId(messageId);
      message.setText("✅ Результат матча сохранен!\n\n"
          + match.getTeam1Name() + " vs " + match.getTeam2Name() + "\n"
          + "Счет: " + score);
      message.setReplyMarkup(keyboardTournamentUtil.getMatchMenu(
          matchId, match.getTournamentId(), "COMPLETED"));
      bot.execute(message);
    } catch (Exception e) {
      sendMessage(chatId, "Ошибка при сохранении результата: " + e.getMessage(), bot);
    }
  }

  private void handleMatchView(String data, String chatId, Integer messageId,
      TelegramLongPollingBot bot) throws TelegramApiException {
    Integer matchId = Integer.parseInt(data.split("_")[2]);
    MatchDto match = matchService.getMatch(matchId);

    StringBuilder text = new StringBuilder("📊 Результат матча:\n\n");
    text.append(match.getTeam1Name()).append(" vs ").append(match.getTeam2Name()).append("\n");
    text.append("Статус: ").append(match.getStatus()).append("\n");

    if ("COMPLETED".equals(match.getStatus())) {
      try {
        MatchResultDto result = matchService.getMatchResult(matchId);
        text.append("\n🏆 Победитель: ").append(result.getWinnerTeamName()).append("\n");
        text.append("Счет: ").append(result.getFinalScore()).append("\n");
        text.append("Очки победителя: ").append(result.getWinnerPoints()).append("\n");
        text.append("Очки проигравшего: ").append(result.getLoserPoints());
      } catch (Exception e) {
        text.append("\n(Результат не найден)");
      }
    }

    EditMessageText message = new EditMessageText();
    message.setChatId(chatId);
    message.setMessageId(messageId);
    message.setText(text.toString());
    message.setReplyMarkup(keyboardTournamentUtil.getMatchMenu(
        matchId, match.getTournamentId(), match.getStatus()));
    bot.execute(message);
  }

  private void handleMatchDispute(String data, String chatId, Integer messageId, Long userId,
      TelegramLongPollingBot bot) throws TelegramApiException {
    Integer matchId = Integer.parseInt(data.split("_")[2]);

    try {
      matchService.disputeResult(matchId);
      MatchDto match = matchService.getMatch(matchId);

      EditMessageText message = new EditMessageText();
      message.setChatId(chatId);
      message.setMessageId(messageId);
      message.setText("⚠️ Результат матча помечен как спорный.\n\n"
          + "Администратор турнира будет уведомлен.");
      message.setReplyMarkup(keyboardTournamentUtil.getMatchMenu(
          matchId, match.getTournamentId(), match.getStatus()));
      bot.execute(message);
    } catch (Exception e) {
      sendMessage(chatId, "Ошибка: " + e.getMessage(), bot);
    }
  }

  private void handleMatchCreate(String data, String chatId, Long userId,
      TelegramLongPollingBot bot) {
    Integer tournamentId = Integer.parseInt(data.split("_")[2]);
    List<TeamDto> teams = teamService.getTeamsByTournament(tournamentId);

    if (teams.size() < 2) {
      sendMessage(chatId, "Для создания матча нужно минимум 2 команды в турнире.", bot);
      return;
    }

    StringBuilder text = new StringBuilder("Для создания матча отправьте сообщение в формате:\n\n");
    text.append("/add_match ").append(tournamentId).append(" ID_команды1 ID_команды2\n\n");
    text.append("Доступные команды:\n");
    for (TeamDto team : teams) {
      text.append(String.format("• %s (ID: %d)\n", team.getName(), team.getId()));
    }
    text.append("\nПример:\n");
    text.append("/add_match ").append(tournamentId).append(" ")
        .append(teams.get(0).getId()).append(" ").append(teams.get(1).getId());

    sendMessage(chatId, text.toString(), bot);
  }

  private void sendMessage(String chatId, String text, TelegramLongPollingBot bot) {
    SendMessage message = new SendMessage();
    message.setChatId(chatId);
    message.setText(text);
    try {
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
    }
  }
}

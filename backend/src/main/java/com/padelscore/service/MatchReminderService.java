package com.padelscore.service;

import com.padelscore.entity.Match;
import com.padelscore.entity.TeamPlayer;
import com.padelscore.repository.MatchRepository;
import com.padelscore.repository.TeamPlayerRepository;
import com.padelscore.telegram.PadelScoreBot;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Сервис напоминаний о запланированных матчах: раз в день в 20:00 отправляет в Telegram участникам
 * команд уведомление о матчах на завтра.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchReminderService {

  private static final DateTimeFormatter TIME_FORMAT =
      DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  private static final String HEADER = "🎾 Напоминание\n Завтра у вас запланированы матчи:\n\n";

  private final MatchRepository matchRepository;

  private final TeamPlayerRepository teamPlayerRepository;

  private final PadelScoreBot padelScoreBot;

  /**
   * Задача по расписанию: каждый день в 20:00 находит матчи на завтра и отправляет напоминание в
   * Telegram участникам соответствующих команд.
   */
  @Scheduled(cron = "0 00 16 * * ?", zone = "Europe/Moscow")
  @Transactional(readOnly = true)
  public void sendTomorrowMatchReminders() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    LocalDateTime start = tomorrow.atStartOfDay();
    LocalDate tomorrowPlusDays = tomorrow.plusDays(1);
    LocalDateTime end = tomorrowPlusDays.atStartOfDay();

    List<Match> matches = matchRepository.findByScheduledDateBetween(start, end);
    if (matches.isEmpty()) {
      log.info("Матчей для уведомлений на {} нет", tomorrow);
      return;
    }

    Map<Long, List<String>> remindersByUser = buildRemindersByUser(matches);
    sendRemindersToUsers(remindersByUser);
  }

  private Map<Long, List<String>> buildRemindersByUser(List<Match> matches) {
    Map<Long, List<String>> remindersByUser = new HashMap<>();
    for (Match match : matches) {
      addMatchReminders(match, remindersByUser);
    }
    return remindersByUser;
  }

  private void addMatchReminders(Match match, Map<Long, List<String>> remindersByUser) {
    String tournamentName = match.getTournament().getTitle();
    String team1Name = match.getTeam1().getName();
    String team2Name = match.getTeam2().getName();
    String timeStr = match.getScheduledDate().format(TIME_FORMAT);
    String location = match.getLocation() != null ? match.getLocation() : "—";

    String lineTeam1 = String.format(
        "🏆 Турнир: %s. Ваша команда «%s» vs «%s».%n⏰ Дата и время: %s.%n📍 Место: %s",
        tournamentName, team1Name, team2Name, timeStr, location);
    String lineTeam2 = String.format(
        "🏆 Турнир: %s. Ваша команда «%s» vs «%s».%n⏰ Дата и время: %s.%n📍 Место: %s",
        tournamentName, team2Name, team1Name, timeStr, location);

    List<TeamPlayer> team1Players =
        teamPlayerRepository.findByTeamId(match.getTeam1().getId());
    List<TeamPlayer> team2Players =
        teamPlayerRepository.findByTeamId(match.getTeam2().getId());

    addLinesForTeamPlayers(team1Players, lineTeam1, remindersByUser);
    addLinesForTeamPlayers(team2Players, lineTeam2, remindersByUser);
  }

  private void addLinesForTeamPlayers(List<TeamPlayer> players, String line,
      Map<Long, List<String>> remindersByUser) {
    for (TeamPlayer tp : players) {
      Long tid = tp.getPlayerProfile().getTelegramId();
      if (tid != null) {
        remindersByUser.computeIfAbsent(tid, k -> new ArrayList<>()).add(line);
      }
    }
  }

  private void sendRemindersToUsers(Map<Long, List<String>> remindersByUser) {
    for (Map.Entry<Long, List<String>> e : remindersByUser.entrySet()) {
      String text = HEADER + String.join("\n\n", e.getValue());
      sendMessage(e.getKey(), text);
    }
  }

  private void sendMessage(Long telegramId, String text) {
    SendMessage message = new SendMessage();
    message.setChatId(telegramId.toString());
    message.setText(text);
    try {
      padelScoreBot.execute(message);
      log.info("Уведомления успешно отправлены");
    } catch (TelegramApiException ex) {
      log.error("Не удалось отправить напоминание о матче пользователю [{}}: {}",
          telegramId, ex.getMessage());
    }
  }
}

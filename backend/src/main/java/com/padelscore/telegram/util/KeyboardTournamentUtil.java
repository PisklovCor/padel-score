package com.padelscore.telegram.util;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import com.padelscore.dto.MatchDto;
import com.padelscore.dto.TeamDto;
import com.padelscore.dto.TeamPlayerDto;
import com.padelscore.dto.TournamentDto;

@Component
public class KeyboardTournamentUtil {

  /**
   * Добавляет кнопки для меня турниров
   *
   * @param tournaments список турниров
   * @return кнопки формы турниров
   */
  public InlineKeyboardMarkup getTournamentsMenu(List<TournamentDto> tournaments) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    for (TournamentDto tournament : tournaments) {
      List<InlineKeyboardButton> row = new ArrayList<>();
      InlineKeyboardButton button = new InlineKeyboardButton();
      button.setText("🏆 " + tournament.getTitle());
      button.setCallbackData("tournament_" + tournament.getId());
      row.add(button);
      keyboard.add(row);
    }

    List<InlineKeyboardButton> backRow = new ArrayList<>();
    InlineKeyboardButton back = new InlineKeyboardButton();
    back.setText("📑 Главное меню");
    back.setCallbackData("menu");
    backRow.add(back);
    keyboard.add(backRow);

    markup.setKeyboard(keyboard);
    return markup;
  }

  /**
   * Добавляет кнопки для меня отдельного турнира
   *
   * @param tournamentId турнир
   * @return кнопки формы турниров
   */
  public InlineKeyboardMarkup getTournamentMenu(Integer tournamentId) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    List<InlineKeyboardButton> row1 = new ArrayList<>();
    InlineKeyboardButton teams = new InlineKeyboardButton();
    teams.setText("👥 Команды");
    teams.setCallbackData("teams_list_" + tournamentId);
    row1.add(teams);

    InlineKeyboardButton matches = new InlineKeyboardButton();
    matches.setText("⚽ Матчи");
    matches.setCallbackData("matches_list_" + tournamentId);
    row1.add(matches);

    List<InlineKeyboardButton> row2 = new ArrayList<>();
    InlineKeyboardButton leaderboard = new InlineKeyboardButton();
    leaderboard.setText("📊 Таблица");
    leaderboard.setCallbackData("leaderboard_" + tournamentId);
    row2.add(leaderboard);

//        List<InlineKeyboardButton> row3 = new ArrayList<>();
//        InlineKeyboardButton addTeam = new InlineKeyboardButton();
//        addTeam.setText("➕ Добавить команду");
//        addTeam.setCallbackData("team_create_" + tournamentId);
//        row3.add(addTeam);
//
//        List<InlineKeyboardButton> row4 = new ArrayList<>();
//        InlineKeyboardButton addMatch = new InlineKeyboardButton();
//        addMatch.setText("➕ Создать матч");
//        addMatch.setCallbackData("match_create_" + tournamentId);
//        row4.add(addMatch);

    List<InlineKeyboardButton> row5 = new ArrayList<>();
    InlineKeyboardButton back = new InlineKeyboardButton();
    back.setText("◀️ Назад к турнирам");
    back.setCallbackData("tournament_list");
    row5.add(back);

    keyboard.add(row1);
    keyboard.add(row2);
//        keyboard.add(row3);
//        keyboard.add(row4);
    keyboard.add(row5);
    markup.setKeyboard(keyboard);
    return markup;
  }

  /**
   * Строит клавиатуру со списком команд турнира и кнопкой «Назад».
   *
   * @param teams        список команд
   * @param tournamentId идентификатор турнира
   * @return разметка inline-кнопок
   */
  public InlineKeyboardMarkup getTeamsMenu(List<TeamDto> teams, Integer tournamentId) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    for (TeamDto team : teams) {
      List<InlineKeyboardButton> row = new ArrayList<>();
      InlineKeyboardButton button = new InlineKeyboardButton();
      button.setText("👥 " + team.getName());
      button.setCallbackData("team_" + team.getId());
      row.add(button);
      keyboard.add(row);
    }

    List<InlineKeyboardButton> backRow = new ArrayList<>();
    InlineKeyboardButton back = new InlineKeyboardButton();
    back.setText("◀️ Назад");
    back.setCallbackData("tournament_" + tournamentId);
    backRow.add(back);
    keyboard.add(backRow);

    markup.setKeyboard(keyboard);
    return markup;
  }

  /**
   * Меню просмотра состава команды: только кнопка «Назад к командам».
   */
  public InlineKeyboardMarkup getTeamViewMenu(Integer tournamentId) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    List<InlineKeyboardButton> backRow = new ArrayList<>();
    InlineKeyboardButton back = new InlineKeyboardButton();
    back.setText("◀️ Назад к командам");
    back.setCallbackData("teams_list_" + tournamentId);
    backRow.add(back);
    keyboard.add(backRow);
    markup.setKeyboard(keyboard);
    return markup;
  }

  /**
   * Строит клавиатуру команды: Игроки, Добавить игрока, «Назад к командам».
   *
   * @param teamId       идентификатор команды
   * @param tournamentId идентификатор турнира
   * @return разметка inline-кнопок
   */
  public InlineKeyboardMarkup getTeamMenu(Integer teamId, Integer tournamentId) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    List<InlineKeyboardButton> row1 = new ArrayList<>();
    InlineKeyboardButton players = new InlineKeyboardButton();
    players.setText("👤 Игроки");
    players.setCallbackData("players_list_" + teamId);
    row1.add(players);

    List<InlineKeyboardButton> row2 = new ArrayList<>();
    InlineKeyboardButton addPlayer = new InlineKeyboardButton();
    addPlayer.setText("➕ Добавить игрока");
    addPlayer.setCallbackData("player_create_" + teamId);
    row2.add(addPlayer);

    List<InlineKeyboardButton> row3 = new ArrayList<>();
    InlineKeyboardButton back = new InlineKeyboardButton();
    back.setText("◀️ Назад к командам");
    back.setCallbackData("teams_list_" + tournamentId);
    row3.add(back);

    keyboard.add(row1);
    keyboard.add(row2);
    keyboard.add(row3);
    markup.setKeyboard(keyboard);
    return markup;
  }

  /**
   * Строит клавиатуру со списком игроков команды, кнопкой «Добавить игрока» и «Назад».
   *
   * @param players список игроков
   * @param teamId  идентификатор команды
   * @return разметка inline-кнопок
   */
  public InlineKeyboardMarkup getPlayersMenu(List<TeamPlayerDto> players, Integer teamId) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    for (TeamPlayerDto player : players) {
      List<InlineKeyboardButton> row = new ArrayList<>();
      InlineKeyboardButton button = new InlineKeyboardButton();
      button.setText("👤 " + player.getFirstName() + " " + player.getLastName());
      button.setCallbackData("player_" + player.getId());
      row.add(button);
      keyboard.add(row);
    }

    List<InlineKeyboardButton> addRow = new ArrayList<>();
    InlineKeyboardButton addPlayer = new InlineKeyboardButton();
    addPlayer.setText("➕ Добавить игрока");
    addPlayer.setCallbackData("player_create_" + teamId);
    addRow.add(addPlayer);
    keyboard.add(addRow);

    List<InlineKeyboardButton> backRow = new ArrayList<>();
    InlineKeyboardButton back = new InlineKeyboardButton();
    back.setText("◀️ Назад");
    back.setCallbackData("team_" + teamId);
    backRow.add(back);
    keyboard.add(backRow);

    markup.setKeyboard(keyboard);
    return markup;
  }

  /**
   * Строит клавиатуру со списком матчей турнира и кнопкой «Назад».
   *
   * @param matches      список матчей
   * @param tournamentId идентификатор турнира
   * @return разметка inline-кнопок
   */
  public InlineKeyboardMarkup getMatchesMenu(List<MatchDto> matches, Integer tournamentId) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    DateTimeFormatter shortDate = DateTimeFormatter.ofPattern("dd.MM HH:mm");
    for (MatchDto match : matches) {
      List<InlineKeyboardButton> row = new ArrayList<>();
      InlineKeyboardButton button = new InlineKeyboardButton();
      String status = "SCHEDULED".equals(match.getStatus()) ? "⏰" :
          "COMPLETED".equals(match.getStatus()) ? "✅" : "🔄";
      String dateStr = match.getScheduledDate() != null
          ? match.getScheduledDate().format(shortDate) : "—";
      button.setText(status + " " + match.getTeam1Name() + " vs " + match.getTeam2Name() + " · " + dateStr);
      button.setCallbackData("match_" + match.getId());
      row.add(button);
      keyboard.add(row);
    }

    List<InlineKeyboardButton> backRow = new ArrayList<>();
    InlineKeyboardButton back = new InlineKeyboardButton();
    back.setText("◀️ Назад");
    back.setCallbackData("tournament_" + tournamentId);
    backRow.add(back);
    keyboard.add(backRow);

    markup.setKeyboard(keyboard);
    return markup;
  }

  /**
   * Строит клавиатуру матча: для scheduled — «Ввести результат»; для completed — «Просмотр
   * результата», «Изменить», «Оспорить»; всегда «Назад к матчам».
   *
   * @param matchId      идентификатор матча
   * @param tournamentId идентификатор турнира
   * @param status       статус матча (scheduled / completed)
   * @return разметка inline-кнопок
   */
  public InlineKeyboardMarkup getMatchMenu(Integer matchId, Integer tournamentId, String status) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    if ("SCHEDULED".equals(status)) {
      List<InlineKeyboardButton> row1 = new ArrayList<>();
      InlineKeyboardButton submitResult = new InlineKeyboardButton();
      submitResult.setText("📝 Ввести результат");
      submitResult.setCallbackData("match_result_" + matchId);
      row1.add(submitResult);
      keyboard.add(row1);
    } else if ("COMPLETED".equals(status)) {
      List<InlineKeyboardButton> row1 = new ArrayList<>();
      InlineKeyboardButton viewResult = new InlineKeyboardButton();
      viewResult.setText("📊 Просмотр результата");
      viewResult.setCallbackData("match_view_" + matchId);
      row1.add(viewResult);

      InlineKeyboardButton editResult = new InlineKeyboardButton();
      editResult.setText("✏️ Изменить результат");
      editResult.setCallbackData("match_result_" + matchId);
      row1.add(editResult);
      keyboard.add(row1);

      List<InlineKeyboardButton> row2 = new ArrayList<>();
      InlineKeyboardButton dispute = new InlineKeyboardButton();
      dispute.setText("⚠️ Оспорить результат");
      dispute.setCallbackData("match_dispute_" + matchId);
      row2.add(dispute);
      keyboard.add(row2);
    }

    List<InlineKeyboardButton> backRow = new ArrayList<>();
    InlineKeyboardButton back = new InlineKeyboardButton();
    back.setText("◀️ Назад к матчам");
    back.setCallbackData("matches_list_" + tournamentId);
    backRow.add(back);
    keyboard.add(backRow);

    markup.setKeyboard(keyboard);
    return markup;
  }

  /**
   * Строит клавиатуру ввода результата матча: счёт 2-0, 2-1, 0-2, 1-2 и «Отмена».
   *
   * @param matchId идентификатор матча
   * @return разметка inline-кнопок
   */
  public InlineKeyboardMarkup getResultInputMenu(Integer matchId) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    List<InlineKeyboardButton> row1 = new ArrayList<>();
    InlineKeyboardButton score20 = new InlineKeyboardButton();
    score20.setText("2-0");
    score20.setCallbackData("result_quick_" + matchId + "_2-0");
    row1.add(score20);

    InlineKeyboardButton score21 = new InlineKeyboardButton();
    score21.setText("2-1");
    score21.setCallbackData("result_quick_" + matchId + "_2-1");
    row1.add(score21);

    List<InlineKeyboardButton> row2 = new ArrayList<>();
    InlineKeyboardButton score02 = new InlineKeyboardButton();
    score02.setText("0-2");
    score02.setCallbackData("result_quick_" + matchId + "_0-2");
    row2.add(score02);

    InlineKeyboardButton score12 = new InlineKeyboardButton();
    score12.setText("1-2");
    score12.setCallbackData("result_quick_" + matchId + "_1-2");
    row2.add(score12);

    List<InlineKeyboardButton> row3 = new ArrayList<>();
    InlineKeyboardButton cancel = new InlineKeyboardButton();
    cancel.setText("❌ Отмена");
    cancel.setCallbackData("match_" + matchId);
    row3.add(cancel);

    keyboard.add(row1);
    keyboard.add(row2);
    keyboard.add(row3);
    markup.setKeyboard(keyboard);
    return markup;
  }
}

package com.padelscore.telegram.util;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
      button.setCallbackData("tournament_card_" + tournament.getId());
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
   * Строит клавиатуру с одной кнопкой «Назад к турниру».
   *
   * @param tournamentId идентификатор турнира
   * @return разметка inline-кнопок
   */
  public InlineKeyboardMarkup getBackToTournamentMenu(Integer tournamentId) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    List<InlineKeyboardButton> row = new ArrayList<>();
    InlineKeyboardButton back = new InlineKeyboardButton();
    back.setText("◀️ Назад к турниру");
    back.setCallbackData("tournament_card_" + tournamentId);
    row.add(back);
    keyboard.add(row);

    markup.setKeyboard(keyboard);
    return markup;
  }
}

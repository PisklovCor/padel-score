package com.padelscore.telegram.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import com.padelscore.dto.TournamentDto;
import com.padelscore.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardTournamentUtil {

  public static final String TOURNAMENT_CARD = "tournament_card_";

  public static final String MENU = "menu";

  public static final String TEAMS_LIST = "teams_list_";

  public static final String MATCHES_LIST = "matches_list_";

  public static final String LEADERBOARD = "leaderboard_";

  public static final String LEADERBOARD_FULL = "leaderboard_full_";

  public static final String TOURNAMENT_LIST = "tournament_list";

  /**
   * Добавляет кнопки для меня турниров
   *
   * @param tournaments список турниров
   * @return кнопки формы турниров
   */
  public InlineKeyboardMarkup getTournamentsMenu(List<TournamentDto> tournaments) {
    InlineKeyboardMarkup markupTournamentsMenu = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardTournamentsMenu = new ArrayList<>();

    for (TournamentDto tournament : tournaments) {
      keyboardTournamentsMenu.add(KeyboardUtil.singleButtonRow("🏆 " + tournament.getTitle(),
          TOURNAMENT_CARD + tournament.getId()));
    }

    keyboardTournamentsMenu.add(KeyboardUtil.singleButtonRow("📑 Главное меню", MENU));

    markupTournamentsMenu.setKeyboard(keyboardTournamentsMenu);
    return markupTournamentsMenu;
  }

  /**
   * Добавляет кнопки для меня отдельного турнира
   *
   * @param tournamentId турнир
   * @return кнопки формы турниров
   */
  public InlineKeyboardMarkup getTournamentMenu(Integer tournamentId) {
    InlineKeyboardMarkup markupTournamentMenu = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardTournamentMenu = new ArrayList<>();

    List<InlineKeyboardButton> matchComposition = new ArrayList<>();
    matchComposition.add(KeyboardUtil.createButton("👥 Команды", TEAMS_LIST + tournamentId));
    matchComposition.add(KeyboardUtil.createButton("⚽ Матчи", MATCHES_LIST + tournamentId));
//        List<InlineKeyboardButton> row3 = new ArrayList<>();
//        InlineKeyboardButton addTeam = new InlineKeyboardButton();
//        addTeam.setText("➕ Добавить команду");
//        addTeam.setCallbackData("team_create_" + tournamentId);
//        row3.add(addTeam);
//        List<InlineKeyboardButton> row4 = new ArrayList<>();
//        InlineKeyboardButton addMatch = new InlineKeyboardButton();
//        addMatch.setText("➕ Создать матч");
//        addMatch.setCallbackData("match_create_" + tournamentId);
//        row4.add(addMatch);
    keyboardTournamentMenu.add(matchComposition);
    keyboardTournamentMenu.add(
        KeyboardUtil.singleButtonRow("📊 Таблица", LEADERBOARD + tournamentId));
    keyboardTournamentMenu.add(
        KeyboardUtil.singleButtonRow("◀️ Назад к турнирам", TOURNAMENT_LIST));
    markupTournamentMenu.setKeyboard(keyboardTournamentMenu);
    return markupTournamentMenu;
  }

  /**
   * Клавиатура для краткой таблицы: «Подробнее» и «Назад к турниру».
   */
  public InlineKeyboardMarkup getLeaderboardKeyboard(Integer tournamentId) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    keyboard.add(KeyboardUtil.singleButtonRow("📋 Подробнее", LEADERBOARD_FULL + tournamentId));
    keyboard.add(KeyboardUtil.singleButtonRow("◀️ Назад к турниру", TOURNAMENT_CARD + tournamentId));
    markup.setKeyboard(keyboard);
    return markup;
  }

  /**
   * Клавиатура для полной таблицы: «Кратко» и «Назад к турниру».
   */
  public InlineKeyboardMarkup getLeaderboardFullKeyboard(Integer tournamentId) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
    keyboard.add(KeyboardUtil.singleButtonRow("📋 Кратко", LEADERBOARD + tournamentId));
    keyboard.add(KeyboardUtil.singleButtonRow("◀️ Назад к турниру", TOURNAMENT_CARD + tournamentId));
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
    InlineKeyboardMarkup markupBackToTournamentMenu = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardBackToTournamentMenu = new ArrayList<>();

    keyboardBackToTournamentMenu.add(
        KeyboardUtil.singleButtonRow("◀️ Назад к турниру", TOURNAMENT_CARD + tournamentId));

    markupBackToTournamentMenu.setKeyboard(keyboardBackToTournamentMenu);
    return markupBackToTournamentMenu;
  }
}

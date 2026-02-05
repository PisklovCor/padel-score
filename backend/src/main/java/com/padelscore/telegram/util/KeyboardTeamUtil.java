package com.padelscore.telegram.util;

import com.padelscore.dto.TeamDto;
import com.padelscore.util.KeyboardUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardTeamUtil {

  public static final String TEAM = "team_";

  public static final String TOURNAMENT_CARD = "tournament_card_";

  public static final String TEAMS_LIST = "teams_list_";

  /**
   * Строит клавиатуру со списком команд турнира и кнопкой «Назад к турниру».
   *
   * @param teams        список команд
   * @param tournamentId идентификатор турнира
   * @return разметка inline-кнопок
   */
  public InlineKeyboardMarkup getTeamsMenu(List<TeamDto> teams, Integer tournamentId) {
    InlineKeyboardMarkup markupTeamsMenu = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardTeamsMenu = new ArrayList<>();

    for (TeamDto team : teams) {
      keyboardTeamsMenu.add(
          KeyboardUtils.singleButtonRow("👥 " + team.getName(), TEAM + team.getId()));
    }

    keyboardTeamsMenu.add(
        KeyboardUtils.singleButtonRow("◀️ Назад к турниру", TOURNAMENT_CARD + tournamentId));

    markupTeamsMenu.setKeyboard(keyboardTeamsMenu);
    return markupTeamsMenu;
  }

  /**
   * Меню просмотра состава команды: только кнопка «Назад к командам».
   *
   * @param tournamentId идентификатор турнира
   * @return разметка inline-кнопок
   */
  public InlineKeyboardMarkup getTeamViewMenu(Integer tournamentId) {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    keyboard.add(KeyboardUtils.singleButtonRow("◀️ Назад к командам", TEAMS_LIST + tournamentId));
    markup.setKeyboard(keyboard);
    return markup;
  }

//  /**
//   * Строит клавиатуру команды: Игроки, Добавить игрока, «Назад к командам».
//   *
//   * @param teamId       идентификатор команды
//   * @param tournamentId идентификатор турнира
//   * @return разметка inline-кнопок
//   */
//  public InlineKeyboardMarkup getTeamMenu(Integer teamId, Integer tournamentId) {
//    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
//    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
//
//    List<InlineKeyboardButton> row1 = new ArrayList<>();
//    InlineKeyboardButton players = new InlineKeyboardButton();
//    players.setText("👤 Игроки");
//    players.setCallbackData("players_list_" + teamId);
//    row1.add(players);
//
//    List<InlineKeyboardButton> row2 = new ArrayList<>();
//    InlineKeyboardButton addPlayer = new InlineKeyboardButton();
//    addPlayer.setText("➕ Добавить игрока");
//    addPlayer.setCallbackData("player_create_" + teamId);
//    row2.add(addPlayer);
//
//    List<InlineKeyboardButton> row3 = new ArrayList<>();
//    InlineKeyboardButton back = new InlineKeyboardButton();
//    back.setText("◀️ Назад к командам");
//    back.setCallbackData("teams_list_" + tournamentId);
//    row3.add(back);
//
//    keyboard.add(row1);
//    keyboard.add(row2);
//    keyboard.add(row3);
//    markup.setKeyboard(keyboard);
//    return markup;
//  }

//  /**
//   * Строит клавиатуру со списком игроков команды, кнопкой «Добавить игрока» и «Назад».
//   *
//   * @param players список игроков
//   * @param teamId  идентификатор команды
//   * @return разметка inline-кнопок
//   */
//  public InlineKeyboardMarkup getPlayersMenu(List<TeamPlayerDto> players, Integer teamId) {
//    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
//    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
//
//    for (TeamPlayerDto player : players) {
//      List<InlineKeyboardButton> row = new ArrayList<>();
//      InlineKeyboardButton button = new InlineKeyboardButton();
//      button.setText("👤 " + player.getFirstName() + " " + player.getLastName());
//      button.setCallbackData("player_" + player.getId());
//      row.add(button);
//      keyboard.add(row);
//    }
//
//    List<InlineKeyboardButton> addRow = new ArrayList<>();
//    InlineKeyboardButton addPlayer = new InlineKeyboardButton();
//    addPlayer.setText("➕ Добавить игрока");
//    addPlayer.setCallbackData("player_create_" + teamId);
//    addRow.add(addPlayer);
//    keyboard.add(addRow);
//
//    List<InlineKeyboardButton> backRow = new ArrayList<>();
//    InlineKeyboardButton back = new InlineKeyboardButton();
//    back.setText("◀️ Назад");
//    back.setCallbackData("team_" + teamId);
//    backRow.add(back);
//    keyboard.add(backRow);
//
//    markup.setKeyboard(keyboard);
//    return markup;
//  }
}

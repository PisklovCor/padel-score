package com.padelscore.telegram.util;

import com.padelscore.dto.MatchDto;
import com.padelscore.util.KeyboardUtil;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardMatchUtil {

  public static final String TOURNAMENT_CARD = "tournament_card_";

  public static final String MATCH_RESULT = "match_result_";

  public static final String MATCH_VIEW = "match_view_";

  public static final String MATCH_DISPUTE = "match_dispute_";

  public static final String MATCHES_LIST = "matches_list_";

  public static final String RESULT_QUICK = "result_quick_";

  public static final String MATCH_CARD = "match_card_";

  /**
   * Строит клавиатуру со списком матчей турнира и кнопкой «Назад к турнирам».
   *
   * @param matches      список матчей
   * @param tournamentId идентификатор турнира
   * @return разметка inline-кнопок
   */
  public InlineKeyboardMarkup getMatchesMenu(List<MatchDto> matches, Integer tournamentId) {
    InlineKeyboardMarkup markupMatchesMenu = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardMatchesMenu = new ArrayList<>();

    DateTimeFormatter shortDate = DateTimeFormatter.ofPattern("dd.MM HH:mm");
    for (MatchDto match : matches) {
      List<InlineKeyboardButton> row = new ArrayList<>();
      InlineKeyboardButton button = new InlineKeyboardButton();
      String status = "SCHEDULED".equals(match.getStatus()) ? "⏰" :
          "COMPLETED".equals(match.getStatus()) ? "✅" : "🔄";
      String dateStr = match.getScheduledDate() != null
          ? match.getScheduledDate().format(shortDate) : "—";
      button.setText(
          status + " " + match.getTeam1Name() + " vs " + match.getTeam2Name() + " · " + dateStr);
      button.setCallbackData("match_card_" + match.getId());
      row.add(button);
      keyboardMatchesMenu.add(row);
    }

    keyboardMatchesMenu.add(
        KeyboardUtil.singleButtonRow("◀️ Назад к турниру", TOURNAMENT_CARD + tournamentId));

    markupMatchesMenu.setKeyboard(keyboardMatchesMenu);
    return markupMatchesMenu;
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
    InlineKeyboardMarkup markupMatchMenu = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardMatchMenu = new ArrayList<>();

    if ("SCHEDULED".equals(status)) {
      keyboardMatchMenu.add(
          KeyboardUtil.singleButtonRow("📝 Ввести результат", MATCH_RESULT + matchId));
    } else if ("COMPLETED".equals(status)) {
      List<InlineKeyboardButton> completedMatchRow = new ArrayList<>();
      completedMatchRow.add(
          KeyboardUtil.createButton("📊 Просмотр результата", MATCH_VIEW + matchId));
      completedMatchRow.add(
          KeyboardUtil.createButton("✏️ Изменить результат", MATCH_RESULT + matchId));
      keyboardMatchMenu.add(completedMatchRow);
      keyboardMatchMenu.add(
          KeyboardUtil.singleButtonRow("⚠️ Оспорить результат", MATCH_DISPUTE + matchId));
    }

    keyboardMatchMenu.add(
        KeyboardUtil.singleButtonRow("◀️ Назад к матчам", MATCHES_LIST + tournamentId));

    markupMatchMenu.setKeyboard(keyboardMatchMenu);
    return markupMatchMenu;
  }

  /**
   * Строит клавиатуру ввода результата матча: счёт 2-0, 2-1, 0-2, 1-2 и «Отмена».
   *
   * @param matchId идентификатор матча
   * @return разметка inline-кнопок
   */
  public InlineKeyboardMarkup getResultInputMenu(Integer matchId) {
    InlineKeyboardMarkup markupResultInputMenu = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardResultInputMenu = new ArrayList<>();

    List<InlineKeyboardButton> firstResultRow = new ArrayList<>();
    firstResultRow.add(KeyboardUtil.createButton("2-0", RESULT_QUICK + matchId + "_2-0"));
    firstResultRow.add(KeyboardUtil.createButton("2-1", RESULT_QUICK + matchId + "_2-1"));

    List<InlineKeyboardButton> secondResultRow = new ArrayList<>();
    secondResultRow.add(KeyboardUtil.createButton("0-2", RESULT_QUICK + matchId + "_0-2"));
    secondResultRow.add(KeyboardUtil.createButton("1-2", RESULT_QUICK + matchId + "_1-2"));

    keyboardResultInputMenu.add(firstResultRow);
    keyboardResultInputMenu.add(secondResultRow);
    keyboardResultInputMenu.add(KeyboardUtil.singleButtonRow("❌ Отмена", MATCH_CARD + matchId));
    markupResultInputMenu.setKeyboard(keyboardResultInputMenu);
    return markupResultInputMenu;
  }
}

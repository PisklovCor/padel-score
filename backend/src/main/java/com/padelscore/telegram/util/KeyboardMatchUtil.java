package com.padelscore.telegram.util;

import com.padelscore.dto.MatchDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardMatchUtil {

  /**
   * Строит клавиатуру со списком матчей турнира и кнопкой «Назад к турнирам».
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
      button.setCallbackData("match_card_" + match.getId());
      row.add(button);
      keyboard.add(row);
    }

    List<InlineKeyboardButton> backRow = new ArrayList<>();
    InlineKeyboardButton back = new InlineKeyboardButton();
    back.setText("◀️ Назад к турниру");
    back.setCallbackData("tournament_card_" + tournamentId);
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
    cancel.setCallbackData("match_card_" + matchId);
    row3.add(cancel);

    keyboard.add(row1);
    keyboard.add(row2);
    keyboard.add(row3);
    markup.setKeyboard(keyboard);
    return markup;
  }
}

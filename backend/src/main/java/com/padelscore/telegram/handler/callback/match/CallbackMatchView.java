package com.padelscore.telegram.handler.callback.match;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.dto.MatchDto;
import com.padelscore.dto.MatchResultDto;
import com.padelscore.service.MatchService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardMatchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackMatchView implements Callback {

  private final MatchService matchService;

  private final KeyboardMatchUtil keyboardMatchUtil;

  /**
   * Совпадение для callback data «match_view_<matchId>».
   */
  @Override
  public boolean coincidence(String command) {
    return command != null && command.startsWith("match_view_");
  }

  /**
   * Просмотр результата матча (победитель, счёт, очки) и клавиатура с действиями.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String data = callbackQuery.getData();
    String chatId = callbackQuery.getMessage().getChatId().toString();
    Integer messageId = callbackQuery.getMessage().getMessageId();

    try {
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
      message.setReplyMarkup(keyboardMatchUtil.getMatchMenu(
          matchId, match.getTournamentId(), match.getStatus()));
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
    }
  }
}

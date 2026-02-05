package com.padelscore.telegram.handler.callback.match;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.dto.MatchDto;
import com.padelscore.service.MatchService;
import com.padelscore.service.PlayerProfileService;
import com.padelscore.service.TournamentService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardMatchUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackQuickResult implements Callback {

  private final MatchService matchService;

  private final PlayerProfileService playerProfileService;

  private final TournamentService tournamentService;

  private final KeyboardMatchUtil keyboardMatchUtil;

  /**
   * Совпадение для callback data «result_quick_<matchId>_<score>».
   */
  @Override
  public boolean coincidence(String command) {
    return command != null && command.startsWith("result_quick_");
  }

  /**
   * Сохраняет результат матча по быстрой кнопке, редактирует сообщение с подтверждением и клавиатурой.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String data = callbackQuery.getData();
    String chatId = callbackQuery.getMessage().getChatId().toString();
    Integer messageId = callbackQuery.getMessage().getMessageId();
    Long userId = callbackQuery.getFrom().getId();

    try {
      String[] parts = data.split("_");
      Integer matchId = Integer.parseInt(parts[2]);
      String score = parts[3];

      Integer playerProfileId = playerProfileService.getPlayerProfileByTelegramId(userId).getId();
      MatchDto match = matchService.getMatch(matchId);

      if (!tournamentService.isTournamentCreator(playerProfileId, match.getTournamentId())) {
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText("❌ У вас нет прав для редактирования результатов матчей.\n\n"
            + "Только создатель турнира может редактировать результаты.");
        message.setReplyMarkup(keyboardMatchUtil.getMatchMenu(
            matchId, match.getTournamentId(), match.getStatus()));
        bot.execute(message);
        return;
      }

      matchService.submitResult(matchId, score, playerProfileId, null);
      match = matchService.getMatch(matchId);

      EditMessageText message = new EditMessageText();
      message.setChatId(chatId);
      message.setMessageId(messageId);
      message.setText("✅ Результат матча сохранен!\n\n"
          + match.getTeam1Name() + " vs " + match.getTeam2Name() + "\n"
          + "Счет: " + score);
      message.setReplyMarkup(keyboardMatchUtil.getMatchMenu(
          matchId, match.getTournamentId(), "COMPLETED"));
      bot.execute(message);
    } catch (Exception e) {
      sendMessage(chatId, "Ошибка при сохранении результата: " + e.getMessage(), bot);
    }
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

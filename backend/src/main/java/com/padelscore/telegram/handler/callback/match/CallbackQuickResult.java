package com.padelscore.telegram.handler.callback.match;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.dto.MatchDto;
import com.padelscore.service.MatchService;
import com.padelscore.service.PlayerProfileService;
import com.padelscore.service.TournamentService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardMatchUtil;
import com.padelscore.util.MessageUtil;
import com.padelscore.util.TelegramExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackQuickResult implements Callback {

  public static final String NOT_ENOUGH_RIGHTS = """
      ❌ У вас нет прав для редактирования результатов матчей.
      
      Только создатель турнира может редактировать результаты.""";

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
   * Сохраняет результат матча по быстрой кнопке, редактирует сообщение с подтверждением и
   * клавиатурой.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();
    final var userId = callbackQuery.getFrom().getId();

    try {
      String[] parts = callbackQuery.getData().split("_");
      Integer matchId = Integer.parseInt(parts[2]);
      String score = parts[3];

      Integer playerProfileId = playerProfileService.getPlayerProfileByTelegramId(userId).getId();
      MatchDto match = matchService.getMatch(matchId);

      if (!tournamentService.isTournamentCreator(playerProfileId, match.getTournamentId())) {
        sendLackAccess(chatId, messageId, bot, match);
        return;
      }

      matchService.submitResult(matchId, score, playerProfileId, null);
      match = matchService.getMatch(matchId);
      savingResults(chatId, messageId, bot, match, score);
    } catch (Exception e) {
      TelegramExceptionHandler.handle(e);
    }
  }

  private void sendLackAccess(String chatId, Integer messageId, TelegramLongPollingBot bot,
      MatchDto match) throws TelegramApiException {
    bot.execute(MessageUtil.createdEditMessageText(chatId, messageId, NOT_ENOUGH_RIGHTS,
        keyboardMatchUtil.getMatchMenu(
            match.getId(), match.getTournamentId(), match.getStatus())));
  }

  private void savingResults(String chatId, Integer messageId, TelegramLongPollingBot bot,
      MatchDto match, String score) throws TelegramApiException {
    bot.execute(MessageUtil.createdEditMessageText(chatId, messageId, "✅ Результат матча сохранен!\n\n"
        + match.getTeam1Name() + " vs " + match.getTeam2Name() + "\n"
        + "Счет: " + score, keyboardMatchUtil.getMatchMenu(
        match.getId(), match.getTournamentId(), "COMPLETED")));
  }
}

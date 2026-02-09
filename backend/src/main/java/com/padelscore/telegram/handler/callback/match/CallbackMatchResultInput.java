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
public class CallbackMatchResultInput implements Callback {

  public static final String NOT_ENOUGH_RIGHTS = """
      ❌ У вас нет прав для редактирования результатов матчей.
      
      Только создатель турнира может редактировать результаты.""";

  private final MatchService matchService;

  private final PlayerProfileService playerProfileService;

  private final TournamentService tournamentService;

  private final KeyboardMatchUtil keyboardMatchUtil;

  /**
   * Совпадение для callback data «match_result_<matchId>».
   */
  @Override
  public boolean coincidence(String command) {
    return command != null && command.startsWith("match_result_");
  }

  /**
   * Выбор результата матча (только для создателя турнира) и клавиатура с вариантами счёта.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    final var data = callbackQuery.getData();
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();
    final var userId = callbackQuery.getFrom().getId();

    try {
      final Integer matchId = Integer.parseInt(data.split("_")[2]);
      MatchDto match = matchService.getMatch(matchId);

      Integer playerProfileId = playerProfileService.getPlayerProfileByTelegramId(userId).getId();
      if (!tournamentService.isTournamentCreator(playerProfileId, match.getTournamentId())) {
        bot.execute(MessageUtil.createdEditMessageText(chatId, messageId, NOT_ENOUGH_RIGHTS,
            keyboardMatchUtil.getMatchMenu(
                matchId, match.getTournamentId(), match.getStatus())));
        return;
      }

      bot.execute(MessageUtil.createdEditMessageText(chatId, messageId,
          "Выберите результат матча:\n\n" + match.getTeam1Name() + " vs " + match.getTeam2Name(),
          keyboardMatchUtil.getResultInputMenu(matchId)));
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

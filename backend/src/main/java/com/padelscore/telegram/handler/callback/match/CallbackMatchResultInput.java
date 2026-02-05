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
public class CallbackMatchResultInput implements Callback {

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
    String data = callbackQuery.getData();
    String chatId = callbackQuery.getMessage().getChatId().toString();
    Integer messageId = callbackQuery.getMessage().getMessageId();
    Long userId = callbackQuery.getFrom().getId();

    try {
      Integer matchId = Integer.parseInt(data.split("_")[2]);
      MatchDto match = matchService.getMatch(matchId);

      Integer playerProfileId = playerProfileService.getPlayerProfileByTelegramId(userId).getId();
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

      EditMessageText message = new EditMessageText();
      message.setChatId(chatId);
      message.setMessageId(messageId);
      message.setText("Выберите результат матча:\n\n"
          + match.getTeam1Name() + " vs " + match.getTeam2Name());
      message.setReplyMarkup(keyboardMatchUtil.getResultInputMenu(matchId));
      bot.execute(message);
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
    } catch (Exception e) {
      sendMessage(chatId, "Ошибка: " + e.getMessage(), bot);
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

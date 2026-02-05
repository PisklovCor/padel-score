package com.padelscore.telegram.handler.callback.match;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.dto.MatchDto;
import com.padelscore.service.MatchService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardMatchUtil;
import com.padelscore.util.TelegramExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackMatchDispute implements Callback {

  private final MatchService matchService;

  private final KeyboardMatchUtil keyboardMatchUtil;

  /**
   * Совпадение для callback data «match_dispute_<matchId>».
   */
  @Override
  public boolean coincidence(String command) {
    return command != null && command.startsWith("match_dispute_");
  }

  /**
   * Помечает результат матча как спорный и клавиатурой с действиями.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    final var data = callbackQuery.getData();
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();

    try {
      Integer matchId = Integer.parseInt(data.split("_")[2]);
      matchService.disputeResult(matchId);
      MatchDto match = matchService.getMatch(matchId);

      EditMessageText message = new EditMessageText();
      message.setChatId(chatId);
      message.setMessageId(messageId);
      message.setText("⚠️ Результат матча помечен как спорный.\n\n"
          + "Администратор турнира будет уведомлен.");
      message.setReplyMarkup(keyboardMatchUtil.getMatchMenu(
          matchId, match.getTournamentId(), match.getStatus()));
      bot.execute(message);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

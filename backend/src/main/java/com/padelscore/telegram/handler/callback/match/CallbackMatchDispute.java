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
import com.padelscore.telegram.util.KeyboardTournamentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackMatchDispute implements Callback {

  private final MatchService matchService;
  private final KeyboardTournamentUtil keyboardTournamentUtil;

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
    String data = callbackQuery.getData();
    String chatId = callbackQuery.getMessage().getChatId().toString();
    Integer messageId = callbackQuery.getMessage().getMessageId();

    try {
      Integer matchId = Integer.parseInt(data.split("_")[2]);
      matchService.disputeResult(matchId);
      MatchDto match = matchService.getMatch(matchId);

      EditMessageText message = new EditMessageText();
      message.setChatId(chatId);
      message.setMessageId(messageId);
      message.setText("⚠️ Результат матча помечен как спорный.\n\n"
          + "Администратор турнира будет уведомлен.");
      message.setReplyMarkup(keyboardTournamentUtil.getMatchMenu(
          matchId, match.getTournamentId(), match.getStatus()));
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

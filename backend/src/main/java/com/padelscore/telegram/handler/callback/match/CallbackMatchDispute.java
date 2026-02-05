package com.padelscore.telegram.handler.callback.match;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import com.padelscore.dto.MatchDto;
import com.padelscore.dto.MatchResultDto;
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
      
      // Проверяем текущее состояние перед изменением
      MatchResultDto currentResult = matchService.getMatchResult(matchId);
      boolean wasAlreadyDisputed = currentResult.getDisputed() != null && currentResult.getDisputed();
      
      matchService.disputeResult(matchId);
      MatchDto match = matchService.getMatch(matchId);

      // Если результат уже был спорным, отправляем другое сообщение
      String messageText = wasAlreadyDisputed
          ? "⚠️ Результат матча уже помечен как спорный.\n\n"
              + "Администратор турнира будет уведомлен."
          : "⚠️ Результат матча помечен как спорный.\n\n"
              + "Администратор турнира уведомлен.";

      EditMessageText message = new EditMessageText();
      message.setChatId(chatId);
      message.setMessageId(messageId);
      message.setText(messageText);
      message.setReplyMarkup(keyboardMatchUtil.getMatchMenu(
          matchId, match.getTournamentId(), match.getStatus()));
      bot.execute(message);
    } catch (TelegramApiRequestException e) {
      // Игнорируем ошибку "message is not modified" - это не критично
      if (e.getMessage() != null && e.getMessage().contains("message is not modified")) {
        log.debug("Message was not modified, ignoring error for match dispute callback");
        return;
      }
      TelegramExceptionHandler.handle(e);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

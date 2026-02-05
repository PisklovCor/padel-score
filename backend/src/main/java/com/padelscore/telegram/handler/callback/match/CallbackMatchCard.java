package com.padelscore.telegram.handler.callback.match;

import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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
public class CallbackMatchCard implements Callback {

  private final MatchService matchService;

  private final KeyboardMatchUtil keyboardMatchUtil;

  /**
   * Совпадение для callback data «match_card_<matchId>».
   */
  @Override
  public boolean coincidence(String command) {
    return command != null && command.startsWith("match_card_");
  }

  /**
   * Карточка матча (команды, статус, дата, место) и клавиатура с действиями.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    final var data = callbackQuery.getData();
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();

    try {
      Integer matchId = Integer.parseInt(data.split("_")[2]);
      MatchDto match = matchService.getMatch(matchId);

      StringBuilder text = new StringBuilder("⚽ Матч: ")
          .append(match.getTeam1Name())
          .append(" vs ")
          .append(match.getTeam2Name())
          .append("\n\n");
      text.append("ID: ").append(match.getId()).append("\n");
      text.append("Статус: ").append(match.getStatus()).append("\n");
      text.append("Формат: ").append(match.getFormat()).append("\n");
      if (match.getScheduledDate() != null) {
        text.append("Дата: ")
            .append(match.getScheduledDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")))
            .append("\n");
      }
      if (match.getLocation() != null && !match.getLocation().isBlank()) {
        text.append("Место: ").append(match.getLocation().trim()).append("\n");
      }

      EditMessageText message = new EditMessageText();
      message.setChatId(chatId);
      message.setMessageId(messageId);
      message.setText(text.toString());
      message.setReplyMarkup(keyboardMatchUtil.getMatchMenu(
          matchId, match.getTournamentId(), match.getStatus()));
      bot.execute(message);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

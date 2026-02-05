package com.padelscore.telegram.handler.callback.tournament;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.dto.TournamentDto;
import com.padelscore.service.TournamentService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardTournamentUtil;
import com.padelscore.util.TelegramExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackTournamentCard implements Callback {

  private final TournamentService tournamentService;

  private final KeyboardTournamentUtil keyboardTournamentUtil;

  /**
   * Совпадение для callback data «tournament_card_<tournamentId>».
   */
  @Override
  public boolean coincidence(String command) {
    return command != null && command.startsWith("tournament_card_");
  }

  /**
   * Редактирует сообщение: карточка турнира (название, ID, формат, приз) и клавиатура с действиями.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    final var data = callbackQuery.getData();
    final var chatId = callbackQuery.getMessage().getChatId().toString();
    final var messageId = callbackQuery.getMessage().getMessageId();

    try {
      Integer tournamentId = Integer.parseInt(data.split("_")[2]);
      TournamentDto tournament = tournamentService.getTournament(tournamentId);

      EditMessageText message = new EditMessageText();
      message.setChatId(chatId);
      message.setMessageId(messageId);
      message.setText(String.format("🏆 Турнир: %s\n\nID: %d\nФормат: %s\nПриз: %s",
          tournament.getTitle(), tournament.getId(),
          tournament.getFormat(), tournament.getPrize()));
      message.setReplyMarkup(keyboardTournamentUtil.getTournamentMenu(tournamentId));
      bot.execute(message);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }
}

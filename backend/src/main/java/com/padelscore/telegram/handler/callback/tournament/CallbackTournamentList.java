package com.padelscore.telegram.handler.callback.tournament;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.padelscore.dto.TournamentDto;
import com.padelscore.service.PlayerProfileService;
import com.padelscore.service.TournamentService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardTournamentUtil;
import com.padelscore.telegram.util.KeyboardUtil;
import com.padelscore.util.ProfileRequiredGuard;
import com.padelscore.util.TelegramExceptionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackTournamentList implements Callback {

  private final TournamentService tournamentService;

  private final KeyboardTournamentUtil keyboardTournamentUtil;

  private final KeyboardUtil keyboardUtil;

  private final PlayerProfileService playerProfileService;

  private final ProfileRequiredGuard profileRequiredGuard;

  /**
   * Совпадение для callback data «tournaments» или «tournament_list».
   */
  @Override
  public boolean coincidence(String command) {
    //TODO: после раздела UI посмотреть может быть они одинаковые
    return "tournaments".equals(command) || "tournament_list".equals(command);
  }

  /**
   * Редактирует сообщение: список турниров (участие команд пользователя) и клавиатуру с турнирами.
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    final var userId = callbackQuery.getFrom().getId();

    try {
      if (profileRequiredGuard.requireProfileForCallback(userId, callbackQuery, bot)) {
        return;
      }
      Integer playerProfileId = playerProfileService.getPlayerProfileByTelegramId(userId).getId();
      List<TournamentDto> tournaments =
          tournamentService.getTournamentsByUserTeams(playerProfileId);

      EditMessageText message = new EditMessageText();
      message.setChatId(callbackQuery.getMessage().getChatId().toString());
      message.setMessageId(callbackQuery.getMessage().getMessageId());

      createTextMessage(message, tournaments);

      bot.execute(message);
    } catch (TelegramApiException e) {
      TelegramExceptionHandler.handle(e);
    }
  }

  /**
   * Создает описания сообщения и доступную клавиатуру.
   */
  private void createTextMessage(EditMessageText editMessageText, List<TournamentDto> tournaments) {
    if (tournaments.isEmpty()) {

      editMessageText.setText("🏆 Турниры\n\nВаши команды пока не участвуют ни в одном турнире.");
      editMessageText.setReplyMarkup(keyboardUtil.getButtonToMenu());
    } else {
      StringBuilder text = new StringBuilder("🏆 Турниры (участие ваших команд)\n\n");
      for (TournamentDto t : tournaments) {
        text.append(String.format("• %s (ID: %d)%n", t.getTitle(), t.getId()));
      }

      editMessageText.setText(text.toString());
      editMessageText.setReplyMarkup(keyboardTournamentUtil.getTournamentsMenu(tournaments));
    }
  }
}

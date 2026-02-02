package com.padelscore.telegram.handler.callback.tournament;

import com.padelscore.dto.TournamentDto;
import com.padelscore.service.PlayerProfileService;
import com.padelscore.service.TournamentService;
import com.padelscore.telegram.handler.callback.Callback;
import com.padelscore.telegram.util.KeyboardTournamentUtil;
import com.padelscore.telegram.util.KeyboardUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackTournaments implements Callback {

  private final TournamentService tournamentService;

  private final KeyboardTournamentUtil keyboardTournamentUtil;

  private final KeyboardUtil keyboardUtil;

  private final PlayerProfileService playerProfileService;

  /**
   * Совпадение для «tournaments», «tournament_list» или «tournament_&lt;id&gt;».
   */
  @Override
  public boolean coincidence(String command) {
    return "tournaments".equals(command)
        || "tournament_list".equals(command)
        || (command != null && command.startsWith("tournament_") && !command.equals(
        "tournament_list"));
  }

  /**
   * Редактирует сообщение: список турниров пользователя или карточку турнира (ID, формат, приз).
   */
  @Override
  public void handle(CallbackQuery callbackQuery, TelegramLongPollingBot bot) {
    String data = callbackQuery.getData();
    String chatId = callbackQuery.getMessage().getChatId().toString();
    Integer messageId = callbackQuery.getMessage().getMessageId();
    Long userId = callbackQuery.getFrom().getId();

    final boolean isProfileExists = playerProfileService.existsByTelegramId(userId);

    try {

      if (!isProfileExists) {

        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(
            "⚠️ У вас пока нет профиля.\n\nВоспользуйтесь пунктом меню для создания профиля.");
        message.setReplyMarkup(keyboardUtil.getButtonToMenu());
        bot.execute(message);
        return;
      }

      if ("tournaments".equals(data) || "tournament_list".equals(data)) {
        List<TournamentDto> tournaments = tournamentService.getTournamentsByUserTeams(userId);
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        if (tournaments.isEmpty()) {
          message.setText("🏆 Турниры\n\nВаши команды пока не участвуют ни в одном турнире.");
          message.setReplyMarkup(keyboardUtil.getButtonToMenu());
        } else {
          StringBuilder text = new StringBuilder("🏆 Турниры (участие ваших команд)\n\n");
          for (TournamentDto t : tournaments) {
            text.append(String.format("• %s (ID: %d)\n", t.getTitle(), t.getId()));
          }
          message.setText(text.toString());
          message.setReplyMarkup(keyboardTournamentUtil.getTournamentsMenu(tournaments));
        }
        bot.execute(message);
      } else if (data.startsWith("tournament_")) {
        Integer tournamentId = Integer.parseInt(data.split("_")[1]);
        TournamentDto tournament = tournamentService.getTournament(tournamentId);
        EditMessageText message = new EditMessageText();
        message.setChatId(chatId);
        message.setMessageId(messageId);
        message.setText(String.format("🏆 Турнир: %s\n\nID: %d\nФормат: %s\nПриз: %s",
            tournament.getTitle(), tournament.getId(),
            tournament.getFormat(), tournament.getPrize()));
        message.setReplyMarkup(keyboardTournamentUtil.getTournamentMenu(tournamentId));
        bot.execute(message);
      }
    } catch (TelegramApiException e) {
      log.error(e.getMessage());
    }
  }
}

package com.padelscore.bot.util;

import com.padelscore.dto.TournamentDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardUtil {
    
    public InlineKeyboardMarkup getMainMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton tournaments = new InlineKeyboardButton();
        tournaments.setText("🏆 Мои турниры");
        tournaments.setCallbackData("tournament_list");
        row1.add(tournaments);
        
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton help = new InlineKeyboardButton();
        help.setText("❓ Помощь");
        help.setCallbackData("help");
        row2.add(help);
        
        keyboard.add(row1);
        keyboard.add(row2);
        markup.setKeyboard(keyboard);
        return markup;
    }
    
    public InlineKeyboardMarkup getTournamentsMenu(List<TournamentDto> tournaments) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        
        for (TournamentDto tournament : tournaments) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("🏆 " + tournament.getTitle());
            button.setCallbackData("tournament_" + tournament.getId());
            row.add(button);
            keyboard.add(row);
        }
        
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀️ Назад");
        back.setCallbackData("main_menu");
        backRow.add(back);
        keyboard.add(backRow);
        
        markup.setKeyboard(keyboard);
        return markup;
    }
    
    public InlineKeyboardMarkup getTournamentMenu(Integer tournamentId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton leaderboard = new InlineKeyboardButton();
        leaderboard.setText("📊 Таблица");
        leaderboard.setCallbackData("leaderboard_" + tournamentId);
        row1.add(leaderboard);
        
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀️ Назад к турнирам");
        back.setCallbackData("tournament_list");
        row2.add(back);
        
        keyboard.add(row1);
        keyboard.add(row2);
        markup.setKeyboard(keyboard);
        return markup;
    }
    
    public InlineKeyboardMarkup getBackToTournamentMenu(Integer tournamentId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀️ Назад к турниру");
        back.setCallbackData("tournament_" + tournamentId);
        row.add(back);
        keyboard.add(row);
        
        markup.setKeyboard(keyboard);
        return markup;
    }
}

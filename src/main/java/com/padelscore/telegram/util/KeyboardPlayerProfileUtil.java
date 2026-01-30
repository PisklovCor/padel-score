package com.padelscore.telegram.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardPlayerProfileUtil {

    public InlineKeyboardMarkup getProfileMenu(boolean isProfileExists) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<InlineKeyboardButton> mainMenuRow = new ArrayList<>();
        InlineKeyboardButton mainMenu = new InlineKeyboardButton();
        mainMenu.setText("📑 Главное меню");
        mainMenu.setCallbackData("main_menu");
        mainMenuRow.add(mainMenu);
        keyboard.add(mainMenuRow);

        if (!isProfileExists) {
            List<InlineKeyboardButton> profileRow = new ArrayList<>();
            InlineKeyboardButton profile = new InlineKeyboardButton();
            profile.setText("➕ Создать профиль");
            profile.setCallbackData("create_profiles");
            profileRow.add(profile);
            keyboard.add(profileRow);
        }

        List<InlineKeyboardButton> helpRow = new ArrayList<>();
        InlineKeyboardButton help = new InlineKeyboardButton();
        help.setText("❓ Помощь по профилю");
        help.setCallbackData("help_profiles");
        helpRow.add(help);
        keyboard.add(helpRow);

        markup.setKeyboard(keyboard);
        return markup;
    }
}

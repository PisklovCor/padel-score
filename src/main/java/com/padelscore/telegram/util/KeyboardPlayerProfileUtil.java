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

    addKeyboardByIsProfileExists(isProfileExists, keyboard);

    markup.setKeyboard(keyboard);
    return markup;
  }

  private void addKeyboardByIsProfileExists(boolean isProfileExists,
      List<List<InlineKeyboardButton>> keyboard) {

    if (isProfileExists) {

      List<InlineKeyboardButton> deleteRow = new ArrayList<>();
      InlineKeyboardButton deleteProfile = new InlineKeyboardButton();
      deleteProfile.setText("🗑 Удалить профиль");
      deleteProfile.setCallbackData("delete_profiles");
      deleteRow.add(deleteProfile);
      keyboard.add(deleteRow);
    } else {

      List<InlineKeyboardButton> profileRow = new ArrayList<>();
      InlineKeyboardButton profile = new InlineKeyboardButton();
      profile.setText("➕ Создать профиль");
      profile.setCallbackData("create_profiles");
      profileRow.add(profile);
      keyboard.add(profileRow);
    }
  }
}

package com.padelscore.telegram.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardPlayerProfileUtil {

  /**
   * Текст предупреждения перед удалением профиля.
   */
  public static final String DELETE_PROFILE_WARNING = """
      ⚠️ Вы уверены?
      
      При удалении профиля будут удалены ваш рейтинг
      и членство во всех командах.""";

  /**
   * Добавляет кнопки для профиля пользователя (в зависимости от наличия профиля)
   *
   * @param isProfileExists признак наличия профиля
   * @return кнопки формы профиля
   */
  public InlineKeyboardMarkup getProfileMenu(boolean isProfileExists) {

    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    List<InlineKeyboardButton> mainMenuRow = new ArrayList<>();
    InlineKeyboardButton mainMenu = new InlineKeyboardButton();
    mainMenu.setText("📑 Главное меню");
    mainMenu.setCallbackData("menu");
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

  /**
   * Клавиатура подтверждения удаления профиля: «Да, удалить» и «Отмена».
   *
   * @return разметка с кнопками подтверждения и отмены
   */
  public InlineKeyboardMarkup getDeleteConfirmKeyboard() {
    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

    List<InlineKeyboardButton> row = new ArrayList<>();
    InlineKeyboardButton confirm = new InlineKeyboardButton();
    confirm.setText("✅ Да, удалить");
    confirm.setCallbackData("delete_profiles_confirm");
    row.add(confirm);
    InlineKeyboardButton cancel = new InlineKeyboardButton();
    cancel.setText("❌ Отмена");
    cancel.setCallbackData("profiles");
    row.add(cancel);
    keyboard.add(row);
    markup.setKeyboard(keyboard);
    return markup;
  }
}

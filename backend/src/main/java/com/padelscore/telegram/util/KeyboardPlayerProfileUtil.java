package com.padelscore.telegram.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import com.padelscore.util.KeyboardUtils;

@Component
public class KeyboardPlayerProfileUtil {

  /**
   * Текст предупреждения перед удалением профиля.
   */
  public static final String DELETE_PROFILE_WARNING = """
      ⚠️ Вы уверены?
      
      При удалении профиля будут удалены ваш рейтинг
      и членство во всех командах.""";

  public static final String MENU = "menu";

  public static final String DELETE_PROFILES = "delete_profiles";

  public static final String CREATE_PROFILES = "create_profiles";

  public static final String DELETE_PROFILES_CONFIRM = "delete_profiles_confirm";

  public static final String PROFILES = "profiles";

  /**
   * Добавляет кнопки для профиля пользователя (в зависимости от наличия профиля)
   *
   * @param isProfileExists признак наличия профиля
   * @return кнопки формы профиля
   */
  public InlineKeyboardMarkup getProfileMenu(boolean isProfileExists) {

    InlineKeyboardMarkup markupProfileMenu = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardProfileMenu = new ArrayList<>();

    keyboardProfileMenu.add(KeyboardUtils.singleButtonRow("📑 Главное меню", MENU));

    addKeyboardByIsProfileExists(isProfileExists, keyboardProfileMenu);

    markupProfileMenu.setKeyboard(keyboardProfileMenu);
    return markupProfileMenu;
  }

  private void addKeyboardByIsProfileExists(boolean isProfileExists,
      List<List<InlineKeyboardButton>> keyboardProfileMenu) {

    if (isProfileExists) {

      keyboardProfileMenu.add(KeyboardUtils.singleButtonRow("🗑 Удалить профиль", DELETE_PROFILES));
    } else {

      keyboardProfileMenu.add(KeyboardUtils.singleButtonRow("➕ Создать профиль", CREATE_PROFILES));
    }
  }

  /**
   * Клавиатура подтверждения удаления профиля: «Да, удалить» и «Отмена».
   *
   * @return разметка с кнопками подтверждения и отмены
   */
  public InlineKeyboardMarkup getDeleteConfirmKeyboard() {
    InlineKeyboardMarkup markupDeleteConfirmKeyboard = new InlineKeyboardMarkup();
    List<List<InlineKeyboardButton>> keyboardDeleteConfirmKeyboard = new ArrayList<>();
    List<InlineKeyboardButton> rowDeleteConfirmKeyboard = new ArrayList<>();

    rowDeleteConfirmKeyboard.add(
        KeyboardUtils.createButton("✅ Да, удалить", DELETE_PROFILES_CONFIRM));
    rowDeleteConfirmKeyboard.add(KeyboardUtils.createButton("❌ Отмена", PROFILES));

    keyboardDeleteConfirmKeyboard.add(rowDeleteConfirmKeyboard);
    markupDeleteConfirmKeyboard.setKeyboard(keyboardDeleteConfirmKeyboard);
    return markupDeleteConfirmKeyboard;
  }
}

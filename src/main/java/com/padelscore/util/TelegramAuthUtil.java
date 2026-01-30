package com.padelscore.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TelegramAuthUtil {

  private static final String HMAC_SHA256 = "HmacSHA256";
  private static final String WEB_APP_DATA = "WebAppData";

  /**
   * Верифицирует Telegram initData используя HMAC-SHA256
   *
   * @param initData строка initData от Telegram
   * @param botToken токен бота для создания секретного ключа
   * @return true если данные валидны
   */
  public static boolean verifyInitData(String initData, String botToken) {
    try {
      String[] pairs = initData.split("&");
      Map<String, String> dataMap = new HashMap<>();
      String hash = null;

      for (String pair : pairs) {
        String[] keyValue = pair.split("=", 2);
        if (keyValue.length == 2) {
          if ("hash".equals(keyValue[0])) {
            hash = keyValue[1];
          } else {
            dataMap.put(keyValue[0], keyValue[1]);
          }
        }
      }

      if (hash == null) {
        return false;
      }

      // Создаем секретный ключ: HMAC-SHA256(botToken, "WebAppData")
      Mac secretKeyMac = Mac.getInstance(HMAC_SHA256);
      SecretKeySpec secretKeySpec = new SecretKeySpec(
          botToken.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
      secretKeyMac.init(secretKeySpec);
      byte[] secretKey = secretKeyMac.doFinal(WEB_APP_DATA.getBytes(StandardCharsets.UTF_8));

      // Сортируем параметры и создаем data-check-string
      String dataCheckString = dataMap.entrySet().stream()
          .sorted(Map.Entry.comparingByKey())
          .map(entry -> entry.getKey() + "=" + entry.getValue())
          .collect(Collectors.joining("\n"));

      // Вычисляем HMAC: HMAC-SHA256(secretKey, dataCheckString)
      Mac mac = Mac.getInstance(HMAC_SHA256);
      SecretKeySpec finalSecretKeySpec = new SecretKeySpec(secretKey, HMAC_SHA256);
      mac.init(finalSecretKeySpec);
      byte[] calculatedHash = mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));

      // Конвертируем в hex строку
      String calculatedHashHex = bytesToHex(calculatedHash);

      return calculatedHashHex.equals(hash);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Извлекает user_id из initData
   */
  public static Long extractUserId(String initData) {
    try {
      String[] pairs = initData.split("&");
      for (String pair : pairs) {
        if (pair.startsWith("user=")) {
          String userJson = java.net.URLDecoder.decode(pair.substring(5), StandardCharsets.UTF_8);
          // Простой парсинг JSON для получения id
          int idIndex = userJson.indexOf("\"id\":");
          if (idIndex != -1) {
            int start = idIndex + 5;
            int end = userJson.indexOf(",", start);
            if (end == -1) {
              end = userJson.indexOf("}", start);
            }
            if (end != -1) {
              String idStr = userJson.substring(start, end).trim();
              return Long.parseLong(idStr);
            }
          }
        }
      }
    } catch (Exception e) {
      // Игнорируем ошибки парсинга
    }
    return null;
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder result = new StringBuilder();
    for (byte b : bytes) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }
}

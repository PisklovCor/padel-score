package com.padelscore.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Утилитарный класс для проверки корректности параметров Telegram Web App.
 * <p>
 * Реализует алгоритм валидации подписи {@code initData}, сформированной
 * Telegram WebApp, исходя из HMAC‑SHA256 и токена бота.
 * Также позволяет извлечь идентификатор пользователя из параметров WebApp.
 */
public class TelegramAuthUtil {

  private static final String HMAC_SHA256 = "HmacSHA256";

  private static final String WEB_APP_DATA = "WebAppData";

  private TelegramAuthUtil() {
  }

  /**
   * Проверяет HMAC‑подпись строки {@code initData}, полученной от Telegram WebApp.
   *
   * @param initData строка инициализации, предоставленная Telegram WebApp
   * @param botToken токен бота, используется как ключ для генерации подписи
   * @return {@code true}, если подпись корректна и данные не были изменены;
   *         {@code false} при ошибке разбора или несовпадении подписи
   */
  public static boolean verifyInitData(String initData, String botToken) {
    try {
      Map<String, String> dataMap = new HashMap<>();
      String hash = extractDataAndHash(initData, dataMap);
      return hash != null && verifyHash(dataMap, hash, botToken);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Разбирает строку параметров и выделяет {@code hash} и остальные значения.
   *
   * @param initData строка с параметрами формата key=value&...
   * @param dataMap   карта, в которую будут помещены все пары, кроме {@code hash}
   * @return значение {@code hash} из исходной строки, либо {@code null}, если не найдено
   */
  private static String extractDataAndHash(String initData, Map<String, String> dataMap) {
    String hash = null;
    for (String pair : initData.split("&")) {
      String[] keyValue = pair.split("=", 2);
      if (keyValue.length < 2) {
        continue;
      }
      if ("hash".equals(keyValue[0])) {
        hash = keyValue[1];
      } else {
        dataMap.put(keyValue[0], keyValue[1]);
      }
    }
    return hash;
  }

  /**
   * Извлекает поле {@code id} из JSON‑фрагмента {@code user=} внутри initData.
   *
   * @param initData исходная строка инициализации Telegram WebApp
   * @return значение {@code user.id}, либо {@code null}, если парсинг невозможен
   */
  public static Long extractUserId(String initData) {
    try {
      for (String pair : initData.split("&")) {
        if (pair.startsWith("user=")) {
          return parseUserId(pair.substring(5));
        }
      }
    } catch (Exception ignored) {
      return null;
    }
    return null;
  }

  /**
   * Выполняет парсинг ID пользователя из JSON‑строки.
   *
   * @param encodedUserJson закодированный URL JSON с полем {@code id}
   * @return значение поля {@code id}, либо {@code null} при ошибке
   */
  private static Long parseUserId(String encodedUserJson) {
    String userJson = java.net.URLDecoder.decode(encodedUserJson, StandardCharsets.UTF_8);
    int idIndex = userJson.indexOf("\"id\":");
    if (idIndex == -1) {
      return null;
    }
    int start = idIndex + 5;
    int end = findIdEnd(userJson, start);
    if (end == -1) {
      return null;
    }
    return Long.parseLong(userJson.substring(start, end).trim());
  }

  /**
   * Находит окончание значения {@code id} в JSON‑строке, до запятой или фигурной скобки.
   *
   * @param json  JSON‑строка
   * @param start позиция начала значения {@code id}
   * @return индекс окончания значения {@code id}, либо {@code -1}, если не найден
   */
  private static int findIdEnd(String json, int start) {
    int commaIndex = json.indexOf(",", start);
    int braceIndex = json.indexOf("}", start);
    if (commaIndex == -1) {
      return braceIndex;
    }
    if (braceIndex == -1) {
      return commaIndex;
    }
    return Math.min(commaIndex, braceIndex);
  }

  /**
   * Проверяет соответствие хэша данных, используя HMAC‑SHA256.
   *
   * @param dataMap  карта параметров initData (без {@code hash})
   * @param hash     переданный хэш из Telegram
   * @param botToken токен бота
   * @return {@code true}, если вычисленный хэш совпадает с переданным
   * @throws NoSuchAlgorithmException если HMAC‑алгоритм недоступен
   * @throws InvalidKeyException      если токен не может быть использован как ключ
   */
  private static boolean verifyHash(Map<String, String> dataMap,
      String hash,
      String botToken)
      throws NoSuchAlgorithmException, InvalidKeyException {

    byte[] secretKey = createSecretKey(botToken);
    String dataCheckString = createDataCheckString(dataMap);
    String calculatedHashHex = calculateHmac(secretKey, dataCheckString);
    return calculatedHashHex.equals(hash);
  }

  /**
   * Создаёт секретный ключ {@code secretKey = HMAC-SHA256(botToken, "WebAppData")}.
   *
   * @param botToken токен Telegram‑бота
   * @return массив байт секретного ключа
   */
  private static byte[] createSecretKey(String botToken)
      throws NoSuchAlgorithmException, InvalidKeyException {

    Mac mac = Mac.getInstance(HMAC_SHA256);
    SecretKeySpec keySpec =
        new SecretKeySpec(botToken.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
    mac.init(keySpec);
    return mac.doFinal(WEB_APP_DATA.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Формирует строку {@code data-check-string}, сортируя пары по ключу.
   *
   * @param dataMap карта параметров данных
   * @return строка объединённых пар {@code key=value}, разделённых переводами строк
   */
  private static String createDataCheckString(Map<String, String> dataMap) {
    return dataMap.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .map(e -> e.getKey() + "=" + e.getValue())
        .collect(Collectors.joining("\n"));
  }

  /**
   * Вычисляет HMAC‑SHA256 подпись строки и возвращает результат в шестнадцатеричном формате.
   *
   * @param key  секретный ключ
   * @param data строка данных для подписи
   * @return hex‑строка результата HMAC‑SHA256
   */
  private static String calculateHmac(byte[] key, String data)
      throws NoSuchAlgorithmException, InvalidKeyException {

    Mac mac = Mac.getInstance(HMAC_SHA256);
    mac.init(new SecretKeySpec(key, HMAC_SHA256));
    byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    return bytesToHex(result);
  }

  /**
   * Конвертирует массив байт в строку шестнадцатеричного формата.
   *
   * @param bytes массив байт
   * @return строка в hex‑формате
   */
  private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}

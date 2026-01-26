# PadelScore Bot

PadelScore — это Telegram бот и Mini App для управления любительскими и клубными турнирами по паделу: команды, матчи,
результаты и статистика.

## Возможности

- Создание турниров и лиг по паделу.
- Регистрация команд и игроков прямо через Telegram.
- Ввод результатов матчей:
    - Быстрый (только финальный счёт).
    - Детальный (по сетам и геймам).
- Автоматический расчёт турнирной таблицы.
- Просмотр статистики команд и игроков.
- Уведомления о новых результатах и предстоящих матчах.
- (Phase 2) Telegram Mini App с визуальным интерфейсом.

## Технологии

- Java 17+
- Spring Boot 3
- PostgreSQL
- Docker
- Telegram Bot API + Telegram WebApp (для Mini App)

## Структура проекта

```text
padelscore/
├── documents
    ├── PRD_PadelScore.md        # Полный PRD
│   └── TECH_PadelScore.md       # Техническое описание
├── README.md                    # Этот файл
├── src/
│   ├── main/java/com/padelscore/...
│   └── main/resources/...
└── docker-compose.yml

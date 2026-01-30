# PadelScore Bot

PadelScore — это Telegram бот для управления любительскими и клубными турнирами по паделу: команды,
матчи, результаты и статистика.

## Возможности (Phase 1 - MVP)

- ✅ Создание турниров и лиг по паделу
- ✅ Регистрация команд и игроков через Telegram
- ✅ Ввод результатов матчей (быстрый вариант)
- ✅ Автоматический расчёт турнирной таблицы
- ✅ Просмотр статистики команд
- ✅ Inline-клавиатуры для навигации

## Технологии

- Java 17+
- Spring Boot 3.2
- PostgreSQL 15
- Docker & Docker Compose
- Telegram Bot API (LongPolling)
- Liquibase для миграций БД

## Быстрый старт

### Требования

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Telegram Bot Token (получить у [@BotFather](https://t.me/BotFather))

### Запуск через Docker Compose

1. Клонируйте репозиторий
2. Создайте файл `.env` в корне проекта:
   ```
   BOT_TOKEN=your_telegram_bot_token_here
   BOT_USERNAME=your_bot_username_here
   ```
3. Запустите:
   ```bash
   docker-compose up -d
   ```

### Запуск локально

1. Установите PostgreSQL и создайте БД:
   ```sql
   CREATE DATABASE padelscore;
   CREATE USER padel_user WITH PASSWORD 'secure_password';
   GRANT ALL PRIVILEGES ON DATABASE padelscore TO padel_user;
   ```

2. Настройте `application.yml` или переменные окружения:
   ```
   BOT_TOKEN=your_token
   BOT_USERNAME=your_username
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/padelscore
   SPRING_DATASOURCE_USERNAME=padel_user
   SPRING_DATASOURCE_PASSWORD=secure_password
   ```

3. Запустите:
   ```bash
   ./mvnw spring-boot:run
   ```

## Команды бота

- `/start` - Главное меню
- `/create_tournament Название` - Создать турнир
- `/my_tournaments` - Список ваших турниров
- `/help` - Справка

## Структура проекта

```text
padelscore/
├── documents/
│   ├── PRD_PadelScore.md        # Product Requirements Document
│   └── TECH_PadelScore.md        # Техническое описание
├── src/
│   ├── main/java/com/padelscore/
│   │   ├── bot/                  # Telegram Bot обработчики
│   │   ├── config/               # Конфигурация Spring
│   │   ├── dto/                  # Data Transfer Objects
│   │   ├── entity/               # JPA Entities
│   │   ├── repository/           # Spring Data JPA репозитории
│   │   ├── service/              # Бизнес-логика
│   │   └── util/                 # Утилиты
│   └── main/resources/
│       ├── application.yml       # Конфигурация приложения
│       └── db/changelog/         # Liquibase миграции
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

## Разработка

### Сборка проекта

```bash
./mvnw clean package
```

### Запуск тестов

```bash
./mvnw test
```

## Лицензия

Проект находится в разработке.

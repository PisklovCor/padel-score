# TECH: PadelScore Telegram Bot

## 1. Архитектура системы

### 1.1 Общая схема

- Telegram клиенты:
    - Chat bot (Phase 1) — inline-клавиатуры, callback queries.
    - Mini App (Phase 2) — WebApp UI (React/TypeScript).
- Backend:
    - Java 17+, Spring Boot 3.
    - REST API.
    - Сервисы: TournamentService, MatchService, StatisticsService, TelegramBotService, NotificationService.
- База данных:
    - PostgreSQL.
    - Таблицы: `tournaments`, `teams`, `players`, `matches`, `match_results`, `set_scores`, `game_scores`, `user_roles`, `audit_logs`.
- Инфраструктура:
    - Docker (отдельно app и PostgreSQL).
    - Развёртывание на удалённую VM.

---

## 2. Модель данных (PostgreSQL)

### 2.1 Основные таблицы

```sql
CREATE TABLE tournaments (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_by BIGINT NOT NULL,  -- Telegram user_id
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    format VARCHAR(50),          -- 'group', 'knockout', 'mixed'
    scoring_system VARCHAR(50),  -- 'points', 'win_loss'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE teams (
    id SERIAL PRIMARY KEY,
    tournament_id INTEGER NOT NULL REFERENCES tournaments(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    captain_id BIGINT NOT NULL,  -- Telegram user_id
    description TEXT,
    color VARCHAR(7),            -- HEX color
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tournament_id, name)
);

CREATE TABLE players (
    id SERIAL PRIMARY KEY,
    team_id INTEGER NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    telegram_id BIGINT,          -- для прямых уведомлений
    rating INTEGER,
    position VARCHAR(50),        -- 'primary', 'reserve', 'coach'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (team_id, first_name, last_name)
);

CREATE TABLE matches (
    id SERIAL PRIMARY KEY,
    tournament_id INTEGER NOT NULL REFERENCES tournaments(id) ON DELETE CASCADE,
    team1_id INTEGER NOT NULL REFERENCES teams(id),
    team2_id INTEGER NOT NULL REFERENCES teams(id),
    scheduled_date TIMESTAMP,
    status VARCHAR(50),          -- 'scheduled', 'in_progress', 'completed', 'cancelled'
    format VARCHAR(50),          -- 'best_of_3_sets', 'best_of_5_sets'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_teams CHECK (team1_id <> team2_id)
);

CREATE TABLE match_results (
    id SERIAL PRIMARY KEY,
    match_id INTEGER NOT NULL UNIQUE REFERENCES matches(id) ON DELETE CASCADE,
    winner_team_id INTEGER NOT NULL REFERENCES teams(id),
    loser_team_id INTEGER NOT NULL REFERENCES teams(id),
    winner_points INTEGER DEFAULT 3,
    loser_points INTEGER DEFAULT 1,
    final_score VARCHAR(50),     -- "2-0", "2-1"
    notes TEXT,
    submitted_by BIGINT NOT NULL, -- Telegram user_id
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    disputed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE set_scores (
    id SERIAL PRIMARY KEY,
    match_id INTEGER NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
    set_number INTEGER NOT NULL,
    team1_games INTEGER NOT NULL,
    team2_games INTEGER NOT NULL,
    tiebreaker_team1 INTEGER,
    tiebreaker_team2 INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (match_id, set_number)
);

CREATE TABLE game_scores (
    id SERIAL PRIMARY KEY,
    set_id INTEGER NOT NULL REFERENCES set_scores(id) ON DELETE CASCADE,
    game_number INTEGER NOT NULL,
    server_team_id INTEGER NOT NULL REFERENCES teams(id),
    team1_points INTEGER NOT NULL DEFAULT 0,
    team2_points INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (set_id, game_number)
);

CREATE TABLE user_roles (
    id SERIAL PRIMARY KEY,
    tournament_id INTEGER NOT NULL REFERENCES tournaments(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL,  -- Telegram user_id
    role VARCHAR(50) NOT NULL, -- 'admin', 'captain', 'player', 'viewer'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tournament_id, user_id)
);

CREATE TABLE audit_logs (
    id SERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,  -- 'tournament', 'match', 'team', 'player'
    entity_id INTEGER NOT NULL,
    action VARCHAR(50) NOT NULL,       -- 'create', 'update', 'delete', 'dispute'
    user_id BIGINT NOT NULL,           -- Telegram user_id
    old_values JSONB,
    new_values JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
```

---

## 3. REST API (для Mini App и внешних интеграций)

### Tournaments
```
GET /api/v1/tournaments # Список турниров
POST /api/v1/tournaments # Создать турнир
GET /api/v1/tournaments/{id} # Детали турнира
PUT /api/v1/tournaments/{id} # Обновить турнир
DELETE /api/v1/tournaments/{id} # Удалить турнир

Teams
GET /api/v1/tournaments/{id}/teams # Команды турнира
POST /api/v1/teams # Создать команду
PUT /api/v1/teams/{id} # Обновить команду
DELETE /api/v1/teams/{id} # Удалить команду

Players
GET /api/v1/teams/{id}/players # Игроки команды
POST /api/v1/players # Добавить игрока
PUT /api/v1/players/{id} # Обновить игрока
DELETE /api/v1/players/{id} # Удалить игрока

Matches
GET /api/v1/tournaments/{id}/matches # Матчи турнира
POST /api/v1/matches # Создать матч
GET /api/v1/matches/{id} # Детали матча
PUT /api/v1/matches/{id}/result # Ввести/обновить результат
PUT /api/v1/matches/{id}/dispute # Пометить результат спорным

Statistics
GET /api/v1/tournaments/{id}/leaderboard # Таблица турнира
GET /api/v1/tournaments/{id}/stats # Общая статистика турнира
GET /api/v1/players/{id}/stats # Статистика игрока
```

### Auth
```
POST /api/v1/auth/telegram # Верификация Telegram initData для Mini App
```

---

## 4. Backend-структура (Spring Boot)

```text
src/
├── main/java/com/padelscore/
│   ├── config/               # Spring configs
│   ├── controller/           # REST endpoints
│   ├── service/              # Business logic
│   │   ├── TournamentService
│   │   ├── TeamService
│   │   ├── MatchService
│   │   ├── StatisticsService
│   │   ├── TelegramBotService
│   │   └── NotificationService
│   ├── repository/           # Spring Data JPA
│   ├── entity/               # JPA entities
│   ├── dto/                  # DTO классы
│   ├── bot/                  # Telegram bot (LongPolling/Webhook)
│   │   ├── PadelScoreBot
│   │   ├── CommandHandler
│   │   └── CallbackHandler
│   ├── exception/            # Custom exceptions + handlers
│   ├── util/                 # Вспомогательные утилиты
│   └── PadelScoreApplication # Main
└── resources/
    ├── application.yml
    ├── application-prod.yml
    └── db/changelog/         # Liquibase миграции
        └── db.changelog-master.xml
```

## 5. Интеграция с Telegram
Используем библиотеку telegrambots + telegrambots-spring-boot-starter.

Режим:

Phase 1: LongPollingBot (проще для деплоя на одну VM).

Возможность перехода на Webhook при необходимости.

Основные сущности:

/start, /create_tournament, /my_tournaments, /help.

CallbackQuery обработчики для inline-кнопок.

## 6. Docker и развёртывание

### 6.1 Docker Compose (dev)
```
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: padelscore
      POSTGRES_USER: padel_user
      POSTGRES_PASSWORD: secure_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    build: .
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/padelscore
      SPRING_DATASOURCE_USERNAME: padel_user
      SPRING_DATASOURCE_PASSWORD: secure_password
      BOT_TOKEN: ${BOT_TOKEN}
      BOT_USERNAME: ${BOT_USERNAME}
    ports:
      - "8080:8080"
    depends_on:
      - postgres

volumes:
  postgres_data:
```

### 6.2 Dockerfile (prod)

Контекст сборки: `backend/`. Файл расположен в `backend/Dockerfile`.

```
FROM eclipse-temurin:17-jdk-alpine AS builder
RUN apk add --no-cache maven
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/padelscore-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## 7. Управление миграциями БД

**Инструмент:** Liquibase (вместо Flyway)

**Преимущества:**
- Поддержка XML/YAML/SQL форматов
- Гибкая модель changesets
- Встроенный rollback механизм
- Поддержка нескольких БД

**Структура:**
```
resources/db/changelog/
├── db.changelog-master.xml
├── 001-initial-schema.xml
└── 002-add-audit-logs.xml
```

**Конфигурация Spring Boot:**
```yaml
spring:
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml
    enabled: true
```

## 8. Безопасность

### 8.1 Аутентификация и авторизация
- Верификация Telegram initData через HMAC-SHA256
- Роли: admin, captain, player, viewer
- Проверка прав доступа на уровне сервисов

### 8.2 Защита данных
- Prepared statements (JPA) для защиты от SQL-инъекций
- Валидация входных данных (Bean Validation)
- Rate limiting: 100 запросов/минуту на пользователя
- Логирование всех изменений результатов в audit_logs

### 8.3 Хранение секретов
- Токены бота в переменных окружения
- Пароли БД через environment variables
- Не хранить секреты в коде/конфигах

## 9. Тестирование

### 9.1 Стратегия
- **Unit тесты:** сервисы, утилиты (JUnit 5, Mockito)
- **Integration тесты:** REST API, репозитории (TestContainers для PostgreSQL)
- **Coverage:** > 80% (JaCoCo)

### 9.2 Структура тестов
```
src/test/java/com/padelscore/
├── unit/
│   ├── service/
│   └── util/
├── integration/
│   ├── controller/
│   └── repository/
└── e2e/
    └── bot/
```

## 10. Логирование и мониторинг

### 10.1 Логирование
- **Формат:** JSON (для парсинга лог-агрегаторами)
- **Уровни:** ERROR, WARN, INFO, DEBUG
- **Ротация:** по размеру (100MB) и времени (7 дней)
- **Логируем:** запросы, ошибки, изменения результатов, аудит

### 10.2 Мониторинг
- **Метрики:** CPU, RAM, response time, error rate
- **Инструменты:** Spring Boot Actuator + Prometheus + Grafana (опционально)
- **Алерты:** ошибки > 5%, latency > 1s, недоступность БД

## 11. Нефункциональные требования (техчасть)

- Response time API (p95) < 500 ms
- DB query (p95) < 100 ms
- Test coverage (unit/integration) > 80%
- Uptime: 99.5%
- Бэкапы БД каждые 6 часов (pg_dump)
- Rate limiting: 100 запросов/минуту на пользователя

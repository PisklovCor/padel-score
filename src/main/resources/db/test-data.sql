-- SQL скрипт для заполнения базы данных тестовыми данными
-- По 4-5 записей в каждой таблице

-- Очистка данных (опционально, раскомментируйте при необходимости)
-- TRUNCATE TABLE game_scores CASCADE;
-- TRUNCATE TABLE set_scores CASCADE;
-- TRUNCATE TABLE match_results CASCADE;
-- TRUNCATE TABLE matches CASCADE;
-- TRUNCATE TABLE team_players CASCADE;
-- TRUNCATE TABLE teams CASCADE;
-- TRUNCATE TABLE user_roles CASCADE;
-- TRUNCATE TABLE tournaments CASCADE;
-- TRUNCATE TABLE player_profiles CASCADE;
-- TRUNCATE TABLE audit_logs CASCADE;

-- 1. Заполнение таблицы tournaments (5 записей)
INSERT INTO tournaments (title, description, created_by, start_date, end_date, format,
                         scoring_system, prize, status, completed, created_at, updated_at)
VALUES ('Весенний турнир 2025', 'Ежегодный весенний турнир по паделю', 123456789,
        '2025-03-01 10:00:00', '2025-03-15 18:00:00', 'knockout', 'points',
        'Призовой фонд 50000 руб', 'active', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Летний чемпионат', 'Открытый летний чемпионат', 987654321, '2025-06-01 09:00:00',
        '2025-06-30 20:00:00', 'group', 'win_loss', 'Кубок и медали', 'active', false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Осенний кубок', 'Турнир для любителей', 111222333, '2025-09-10 11:00:00',
        '2025-09-25 19:00:00', 'mixed', 'points', 'Сертификаты', 'planned', false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Зимний турнир', 'Закрытый зимний турнир', 444555666, '2024-12-01 10:00:00',
        '2024-12-20 17:00:00', 'knockout', 'points', 'Призовой фонд 30000 руб', 'completed', true,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Новогодний марафон', 'Турнир на новогодние праздники', 777888999, '2024-12-25 12:00:00',
        '2025-01-05 18:00:00', 'group', 'win_loss', 'Подарочные сертификаты', 'completed', true,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. Заполнение таблицы player_profiles (5 записей)
INSERT INTO player_profiles (first_name, last_name, telegram_id, nickname, rating, created_at,
                             updated_at)
VALUES ('Иван', 'Петров', 123456789, 'ivan_padel', 1500, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Мария', 'Сидорова', 987654321, 'maria_pro', 1650, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Алексей', 'Козлов', 111222333, 'alex_champ', 1800, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Елена', 'Новикова', 444555666, 'elena_ace', 1550, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Дмитрий', 'Морозов', 777888999, 'dmitry_win', 1700, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Заполнение таблицы teams (6 записей - добавлена команда для валидного матча в турнире 3)
INSERT INTO teams (tournament_id, name, captain_id, description, color, created_at, updated_at)
VALUES (1, 'Молния', 123456789, 'Быстрая и агрессивная команда', '#FF0000', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (1, 'Тигры', 987654321, 'Сильная команда с опытом', '#FFA500', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (2, 'Орлы', 111222333, 'Команда высшего уровня', '#0000FF', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (2, 'Волки', 444555666, 'Сплоченная команда', '#800080', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (3, 'Соколы', 777888999, 'Молодая перспективная команда', '#008000', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP),
       (3, 'Ястребы', 123456789, 'Опытная команда', '#FFD700', CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- 4. Заполнение таблицы team_players (6 записей)
INSERT INTO team_players (team_id, player_profile_id, position, joined_at)
VALUES (1, 1, 'captain', CURRENT_TIMESTAMP),
       (1, 2, 'player', CURRENT_TIMESTAMP),
       (2, 3, 'captain', CURRENT_TIMESTAMP),
       (2, 4, 'player', CURRENT_TIMESTAMP),
       (5, 5, 'captain', CURRENT_TIMESTAMP),
       (6, 1, 'captain', CURRENT_TIMESTAMP);

-- 5. Заполнение таблицы matches (7 записей - добавлены завершенные матчи для результатов)
-- Примечание: команды должны быть из одного турнира
INSERT INTO matches (tournament_id, team1_id, team2_id, scheduled_date, status, format, location,
                     completed, created_at, updated_at)
VALUES (1, 1, 2, '2025-03-05 14:00:00', 'scheduled', 'best_of_3', 'Корт №1', false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (1, 1, 2, '2025-03-08 16:00:00', 'in_progress', 'best_of_3', 'Корт №2', false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 3, 4, '2025-06-05 15:00:00', 'completed', 'best_of_5', 'Центральный корт', true,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 3, 4, '2025-06-10 17:00:00', 'completed', 'best_of_3', 'Корт №3', true,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 3, 4, '2025-06-12 18:00:00', 'completed', 'best_of_3', 'Корт №2', true,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 3, 4, '2025-06-15 16:00:00', 'completed', 'best_of_5', 'Центральный корт', true,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 5, 6, '2025-09-15 13:00:00', 'scheduled', 'best_of_3', 'Корт №1', false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 6. Заполнение таблицы match_results (4 записи - только для завершенных матчей)
-- Примечание: match_id должен быть уникальным (один результат на матч)
INSERT INTO match_results (match_id, winner_team_id, loser_team_id, winner_points, loser_points,
                           final_score, notes, submitted_by, submitted_at, disputed, created_at,
                           updated_at)
VALUES (3, 3, 4, 3, 1, '6-4, 6-3, 4-6, 6-2', 'Отличная игра обеих команд', 111222333,
        CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, 4, 3, 3, 0, '6-2, 6-1, 6-0', 'Доминирующая победа', 444555666, CURRENT_TIMESTAMP, false,
        CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (5, 3, 4, 3, 2, '6-7, 7-5, 6-4, 3-6, 6-3', 'Напряженный матч до последнего гейма', 111222333,
        CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (6, 3, 4, 3, 1, '6-3, 6-4, 5-7, 6-1', 'Стабильная игра победителей', 111222333,
        CURRENT_TIMESTAMP, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 7. Заполнение таблицы set_scores (5 записей)
-- Примечание: комбинация match_id и set_number должна быть уникальной
INSERT INTO set_scores (match_id, set_number, team1_games, team2_games, tiebreaker_team1,
                        tiebreaker_team2, created_at, updated_at)
VALUES (3, 1, 6, 4, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 2, 6, 3, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (3, 3, 4, 6, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (4, 1, 6, 2, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (5, 1, 7, 5, 7, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 8. Заполнение таблицы game_scores (5 записей)
INSERT INTO game_scores (set_id, game_number, server_team_id, team1_points, team2_points,
                         created_at, updated_at)
VALUES (1, 1, 3, 40, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (1, 2, 4, 15, 40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (1, 3, 3, 40, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 1, 3, 40, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (2, 2, 4, 30, 40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 9. Заполнение таблицы user_roles (5 записей)
INSERT INTO user_roles (tournament_id, user_id, role, created_at)
VALUES (1, 123456789, 'admin', CURRENT_TIMESTAMP),
       (1, 987654321, 'organizer', CURRENT_TIMESTAMP),
       (2, 111222333, 'admin', CURRENT_TIMESTAMP),
       (2, 444555666, 'referee', CURRENT_TIMESTAMP),
       (3, 777888999, 'organizer', CURRENT_TIMESTAMP);

-- 10. Заполнение таблицы audit_logs (5 записей)
INSERT INTO audit_logs (entity_type, entity_id, action, user_id, old_values, new_values, created_at)
VALUES ('Tournament', 1, 'CREATE', 123456789, NULL, '{
  "title": "Весенний турнир 2025",
  "status": "active"
}', CURRENT_TIMESTAMP),
       ('Team', 1, 'CREATE', 123456789, NULL, '{
         "name": "Молния",
         "tournament_id": 1
       }', CURRENT_TIMESTAMP),
       ('Match', 3, 'UPDATE', 111222333, '{
         "status": "in_progress"
       }', '{
         "status": "completed"
       }', CURRENT_TIMESTAMP),
       ('PlayerProfile', 1, 'UPDATE', 123456789, '{
         "rating": 1450
       }', '{
         "rating": 1500
       }', CURRENT_TIMESTAMP),
       ('MatchResult', 1, 'CREATE', 111222333, NULL, '{
         "winner_team_id": 3,
         "final_score": "6-4, 6-3"
       }', CURRENT_TIMESTAMP);

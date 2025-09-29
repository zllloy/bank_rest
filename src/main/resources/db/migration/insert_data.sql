-- Заполнение таблицы users (пользователи) для всех пароль "qwe"
INSERT INTO users_table (username, email, password , enabled, role_id)
VALUES ('Рамиль Джапаров', 'rdb03@mail.ru', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', true, 1),
       ('Иван Петров', 'ivan@example.com', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', true, 2),
       ('Мария Сидорова', 'maria@example.com', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', true, 2),
       ('Алексей Иванов', 'alex@example.com', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', true, 2),
       ('Елена Кузнецова', 'elena@example.com', '$2a$12$WB2YUbFcCN0tm44SBcKUjua9yiFBsfB3vW02IjuwzY7HGtlQIKzy2', true, 2);

INSERT INTO cards_table (number, owner,validity_period, status, balance)
VALUES ('oevJ6fTFiGQvNW/3l3/iVvJY4l7LAeNpLEEfoDbUvR8eH/PLrOLlyjmtFRXGCE5t', 2, '2025-12-31', 'ACTIVE', 15000.00),
       ('/ZYERZjXw4wt9JX+cxpq2vf2oxmk8TBk9IpQ7U1mRWeBTbFguNwYQrRu22+AysyZ', 3,'2026-03-31', 'ACTIVE', 25000.50),
       ('fPtl46x0DMmIvMpcGQkxkQSGuc2wEBqbLaeEoJnB2PgrSGpUeYiTAV3zm1VCpaes', 4, '2025-08-30','BLOCKED', 10000.00),
       ('Ps8exnW4CRXa/9JdKE+OUh+VN7aLvJqR+UOTR+OI6wJfoVkm1CJ+mO3c5t0kww92', 5,'2025-06-30', 'EXPIRED', 0.00);

INSERT INTO requests_to_block_card_table (owner, card)
VALUES (2, 1),
        (3, 2);
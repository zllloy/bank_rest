# BankCards API

## 📌 Описание
Проект для управления банковскими картами.  
Документация доступна в Swagger UI и в файле [docs/openapi.yaml](docs/openapi.yaml).

---

## 🚀 Запуск проекта

### 1. Клонирование репозитория
```bash
git clone https://github.com/username/bankcards.git
cd bankcards
```
### 2. Сборка и запуск
#### Maven
```
./mvnw spring-boot:run
```

### 3. 📖 Документация
### Swagger UI

Доступна после запуска приложения:
http://localhost:8080/swagger-ui/index.html

OpenAPI спецификация

JSON: http://localhost:8080/v3/api-docs

YAML: http://localhost:8080/v3/api-docs.yaml

Актуальная версия спецификации сохранена в проекте:
docs/openapi.yaml

### 🛠 Технологии

Java 17+

Spring Boot

Spring Data JPA

Springdoc OpenAPI (Swagger UI)

Maven / Gradle


----------------------------------------------------------------------------------


## 🚀 Развертывание через Docker

### 1. Клонирование репозитория
```bash
git clone https://github.com/username/bankcards.git
cd bankcards
```

## 2. Сборка и запуск контейнеров
В корне проекта есть docker-compose.yml.

Поднимем базу данных и приложение:
``sh
docker compose up -d --build
``

После этого:

Приложение будет доступно: http://localhost:8080
PostgreSQL будет доступен на порту 5432 (для подключения через pgAdmin или psql)

## 3. Остановка контейнеров
``
docker-compose down
``

## 🧪 Тестирование
### Юнит-тесты
Для проверки бизнес-логики выполните:
``
mvn test
``
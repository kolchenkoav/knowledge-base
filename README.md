
<img src="/img/the-concept-of-the--knowledge-base-project--logo--.png" alt="" width="500" height="500">



![Java](https://img.shields.io/badge/Java-21-blue) 
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen) 
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17.4-blue) 
![Docker](https://img.shields.io/badge/Docker-Supported-blue)  
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Supported-orange)  

**Knowledge Base** — это веб-приложение для хранения, поиска и управления базой знаний, разработанное на **Java 21** с использованием **Spring Boot**, **PostgreSQL**, **Thymeleaf** и **Docker**.  

## 📌 Функциональность

- Хранение вопросов и ответов в базе данных
- Категоризация по темам (Java, Spring, Docker, Git и др.)
- Поиск и фильтрация записей
- Поддержка Markdown для форматирования записей
- Экспорт и импорт данных в JSON и Markdown
- REST API с OpenAPI-документацией
- **Frontend на Thymeleaf** для удобного взаимодействия с базой знаний  

## 🚀 Технологии

- **Backend**: Spring Boot, Spring Data JPA, Spring MVC
- **Frontend**: Thymeleaf, HTML, CSS, Bootstrap
- **База данных**: PostgreSQL
- **ORM**: Hibernate
- **Документация API**: Springdoc OpenAPI
- **Логирование**: SLF4J + Logback
- **Контейнеризация**: Docker Compose
- **Тестирование**: Spring Boot Test, JUnit

## 🖥️ Веб-интерфейс

Приложение содержит формы для работы с базой знаний:  

| Файл | Описание |
|------|---------|
| `knowledge-list.html` | Список записей базы знаний с возможностью фильтрации |
| `knowledge-form.html` | Просмотр отдельной записи (в HTML-формате) |
| `knowledge-edit-form.html` | Форма редактирования записи |
| `knowledge-create-form.html` | Форма добавления новой записи |

**Доступ после запуска:**  
📌 [http://localhost:8085/api/v2/all](http://localhost:8085/api/v2/all) — список всех записей  

## 🛠 Установка и запуск

### 1. Клонирование репозитория
```sh
git clone https://github.com/your-repo/knowledge-base.git
cd knowledge-base
```

### 2. Запуск через Docker
```sh
docker-compose up --build
```
Сервис будет доступен по адресу [http://localhost:8085](http://localhost:8085).

### 3. Локальный запуск (без Docker)
#### Убедитесь, что установлен PostgreSQL и создана БД:
```sql
CREATE DATABASE knowledgedb;
```
#### Настройка переменных окружения (или `application.yml`):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/knowledgedb
    username: postgres
    password: postgres
```
#### Запуск приложения:
```sh
./mvnw spring-boot:run
```

## 🔥 API Документация
Swagger-документация доступна после запуска сервиса:
- [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

## 📄 Использование API

### Получение всех записей
```http
GET /api/v1/all
```

### Получение записи по ID
```http
GET /api/v1/{id}
```

### Создание новой записи
```http
POST /api/v1
Content-Type: application/json

{
  "question": "Что такое Spring Boot?",
  "answer": "Spring Boot — это фреймворк...",
  "bookmark": false,
  "topic": "Spring"
}
```

### Обновление записи
```http
PATCH /api/v1/{id}
Content-Type: application/json

{
  "answer": "Spring Boot упрощает разработку..."
}
```

### Удаление записи
```http
DELETE /api/v1/{id}
```

## 📂 Структура проекта

```
knowledge-base
│── src/main/java/av/kolchenko/base
│   ├── entity          # Сущности JPA
│   ├── repository      # Репозитории Spring Data
│   ├── service         # Логика приложения
│   ├── web/controller  # REST-контроллеры
│   ├── web/dto         # DTO-объекты
│── src/main/resources
│   ├── templates       # HTML-шаблоны Thymeleaf (frontend)
│   │   ├── knowledge-list.html
│   │   ├── knowledge-form.html
│   │   ├── knowledge-edit-form.html
│   │   ├── knowledge-create-form.html
│   ├── application.yml # Конфигурация Spring Boot
│── docker-compose.yaml # Контейнеризация
│── pom.xml             # Maven зависимости
```

## 🧪 Тестирование
Для запуска тестов выполните:
```sh
./mvnw test
```

## 🎯 TODO
- [ ] Добавить кэширование с Redis
- [ ] Реализовать аутентификацию через Spring Security
- [ ] Добавить поддержку Elasticsearch для быстрого поиска

## 👨‍💻 Контакты
Разработчик: **автор проекта**  
Email: `your.email@example.com`  
GitHub: [your-github](https://github.com/your-github)

---

📌 *Звездите репозиторий ⭐, если проект оказался полезным!*
```

Теперь README включает информацию о **Frontend (Thymeleaf)** и ссылку на страницу списка записей. Если нужно еще что-то уточнить — дай знать! 🚀